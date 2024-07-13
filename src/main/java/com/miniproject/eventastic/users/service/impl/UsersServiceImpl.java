package com.miniproject.eventastic.users.service.impl;

import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.exceptions.trx.OrganizerWalletNotFoundException;
import com.miniproject.eventastic.exceptions.trx.PointsTrxNotFoundException;
import com.miniproject.eventastic.exceptions.trx.PointsWalletNotFoundException;
import com.miniproject.eventastic.exceptions.user.DuplicateCredentialsException;
import com.miniproject.eventastic.exceptions.user.ReferralCodeUnusedException;
import com.miniproject.eventastic.exceptions.user.UserNotFoundException;
import com.miniproject.eventastic.image.entity.ImageUserAvatar;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.image.service.CloudinaryService;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.mail.service.MailService;
import com.miniproject.eventastic.mail.service.entity.dto.MailTemplate;
import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.organizerWallet.entity.dto.OrganizerWalletDisplayDto;
import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.service.impl.PointsWalletService;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageSummaryDto;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUseCountDto;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsersDto;
import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.event.UserRegistrationEvent;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
@Slf4j
@Transactional
public class UsersServiceImpl implements UsersService {


  private final UsersRepository usersRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final ReferralCodeUsageService referralCodeUsageService;
  private final ApplicationEventPublisher eventPublisher;
  private final PointsWalletService pointsWalletService;
  private final CloudinaryService cloudinaryService;
  private final ImageService imageService;
  private final PointsTrxService pointsTrxService;
  private final OrganizerWalletService organizerWalletService;
  private final AttendeeService attendeeService;
  private final MailService mailService;

  @Override
  public Users getCurrentUser() throws AccessDeniedException, UserNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new AccessDeniedException("You must be logged in to access this resource");
    }
    String username = authentication.getName();
    return getByUsername(username);
  }

  @Override
  @Transactional
  public UserProfileDto getProfile() throws UserNotFoundException, AccessDeniedException {
    Users user = getCurrentUser();
    return new UserProfileDto(user);
  }

  @Override
  public Users getByUsername(String username) throws UserNotFoundException {
    Optional<Users> usersOptional = usersRepository.findByUsername(username);
    return usersOptional.orElseThrow(() -> new UserNotFoundException(
        "User by username: " + username + " not found. Please ensure you've entered the correct username!"));
  }

  @Override
  public Users getById(Long id) throws UserNotFoundException {
    Optional<Users> usersOptional = usersRepository.findById(id);
    return usersOptional.orElseThrow(() -> new UserNotFoundException(
        "User by ID: " + id + " not found. Please ensure you've entered the correct ID!"));
  }

  @SneakyThrows
  @Override
  @Transactional
  public RegisterResponseDto register(RegisterRequestDto requestDto)
      throws RuntimeException {
    // will throw UserNotFoundException, DuplicateCredentialsException, PointsWalletNotFoundException
    try {
      // check credentials
      String username = requestDto.getUsername();
      String email = requestDto.getEmail();
      Optional<Users> usersOptional = usersRepository.findByUsername(username);
      Optional<Users> usersOptional2 = usersRepository.findByEmail(email);
      if (usersOptional.isPresent() || usersOptional2.isPresent()) {
        throw new DuplicateCredentialsException("Username or email already exists");
      }

      // init new user and the dto
      Users newUser = new Users();
      RegisterRequestDto reqToUser = new RegisterRequestDto();
      reqToUser.toEntity(newUser, requestDto);

      // encode password and save user so that it persists in db
      newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
      usersRepository.save(newUser);
      eventPublisher.publishEvent(new UserRegistrationEvent(this, newUser));

      log.info("Registered user: {}", newUser);

      // * send email
      String fullName = newUser.getFullName();
      MailTemplate welcomeMail = new MailTemplate();
      // ! TODO : uncomment in production, suspend email sending for local
//      mailService.sendEmail(welcomeMail.buildWelcomeTemp(email, fullName));

      return new RegisterResponseDto(newUser);
    } catch (Exception e) {
      log.error(e.getClass().getSimpleName() + e.getMessage());
      log.error("Registration failed for user {}", requestDto.getUsername());
      throw e;
    }
  }

  @Override
  public void saveUser(Users user) {
    usersRepository.save(user);
  }

  @Override
  public void resetPassword(Users user, String newPassword) {
    user.setPassword(passwordEncoder.encode(newPassword));
    usersRepository.save(user);
  }

  @Override
  public void update(ProfileUpdateRequestDTO requestDto)
      throws ImageNotFoundException, UserNotFoundException, AccessDeniedException, DuplicateCredentialsException,
      IllegalArgumentException {
    Users existingUser = getCurrentUser();
    ProfileUpdateRequestDTO update = new ProfileUpdateRequestDTO();
    update.dtoToEntity(existingUser, requestDto);

    // check for image
    if (requestDto.getAvatarId() != null) {
      ImageUserAvatar avatar = imageService.getAvatarById(requestDto.getAvatarId());
      if (avatar != null) {
        existingUser.setAvatar(avatar);
      } else {
        throw new ImageNotFoundException(
            "ImageUserAvatar doesn't exist in database. Please enter another imageId or upload a "
            + "new image");
      }
    }
    usersRepository.save(existingUser);
  }


  // ref code related
  @Override
  public Users getUserByOwnedCode(String ownedCode) {
    return usersRepository.findByOwnedRefCode(ownedCode).orElse(null);
  }

  @Override
  public ReferralCodeUsageSummaryDto getCodeUsageSummary()
      throws UserNotFoundException, AccessDeniedException, ReferralCodeUnusedException {
    Users codeOwner = getCurrentUser();
    log.info("CodeOwner: {}", codeOwner);

    ReferralCodeUseCountDto owner = referralCodeUsageService.getReferralCodeUseCount(codeOwner);
    List<ReferralCodeUsersDto> usedBy = referralCodeUsageService.getReferralCodeUsers(codeOwner);

    return new ReferralCodeUsageSummaryDto(owner, usedBy);
  }

  @SneakyThrows
  @Override
  public PointsWallet getUsersPointsWallet()
      throws UserNotFoundException, AccessDeniedException, PointsWalletNotFoundException {
    // get currently logged-in user
    Users currentUser = getCurrentUser();
    return pointsWalletService.getPointsWallet(currentUser);
  }

  @Override
  public ImageUserAvatar uploadAvatar(ImageUploadRequestDto requestDto) throws IllegalArgumentException {
    Users user = getCurrentUser();
    return imageService.uploadAvatar(requestDto, user);
  }

  @Override
  public Set<PointsTrx> getPointsTrx() throws PointsTrxNotFoundException {
    PointsWallet pointsWallet = getUsersPointsWallet();
    Set<PointsTrx> pointsTrxes = pointsTrxService.getPointsTrx(pointsWallet);
    if (pointsTrxes.isEmpty()) {
      throw new PointsTrxNotFoundException("No history of points usage is found!");
    } else {
      return pointsTrxes;
    }
  }

  @Override
  public OrganizerWalletDisplayDto getWalletDisplay() throws UserNotFoundException, OrganizerWalletNotFoundException,
      AccessDeniedException {
    Users organizer = getCurrentUser();
    OrganizerWallet organizerWallet;
    if (!organizer.getIsOrganizer()) {
      throw new AccessDeniedException("Only organizers can access this wallet");
    } else {
      organizerWallet = organizerWalletService.getWalletByOrganizer(organizer);
      if (organizerWallet == null) {
        throw new OrganizerWalletNotFoundException("Wallet not found! Or are you an impostor..");
      } else {
        return new OrganizerWalletDisplayDto(organizerWallet);
      }
    }
  }

  @Override
  public Page<EventResponseDto> getAttendeeEvents(int page, int size) throws UserNotFoundException,
      AccessDeniedException, EventNotFoundException {
    Users currentUser = getCurrentUser();
    Long userId = currentUser.getId();
    Pageable pageable = PageRequest.of(page, size);
    Page<Event> eventsPage = attendeeService.findEventsByAttendee(userId, pageable);
    return eventsPage.map(EventResponseDto::new);
  }

}
