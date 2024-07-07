package com.miniproject.eventastic.users.service.impl;

import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.exceptions.trx.OrganizerWalletNotFound;
import com.miniproject.eventastic.exceptions.trx.PointsTrxNotFoundException;
import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.image.service.CloudinaryService;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.organizerWallet.entity.dto.InitOrganizerWalletDto;
import com.miniproject.eventastic.organizerWallet.entity.dto.OrganizerWalletDisplayDto;
import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
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

  @Override
  public List<UserProfileDto> getAllUsers() {
    List<Users> users = usersRepository.findAll();
    if (users.isEmpty()) {
      throw new EmptyResultDataAccessException("No users found", 1);
    }
    return users.stream()
        .map(UserProfileDto::new)
        .collect(Collectors.toList());
  }

  @Override
  public UserProfileDto getProfile() {
    // * get logged in user
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    // * check
    Optional<Users> user = usersRepository.findByUsername(username);
    if (user.isPresent()) {
      Users loggedUser = user.get();
      UserProfileDto userProfileDto = new UserProfileDto();
      return userProfileDto.toDto(loggedUser);
    } else {
      return null;
    }
  }

  @Override
  public Users getByUsername(String username) {
    Optional<Users> usersOptional = usersRepository.findByUsername(username);
    return usersOptional.orElse(null);
  }

  @Override
  public Users getById(Long id) {
    Optional<Users> usersOptional = usersRepository.findById(id);
    return usersOptional.orElse(null);
  }

  @SneakyThrows
  @Override
  public RegisterResponseDto register(RegisterRequestDto requestDto) {
    // init new user and the dto
    Users newUser = new Users();
    RegisterRequestDto reqToUser = new RegisterRequestDto();
    reqToUser.toEntity(newUser, requestDto);

    // encode password and save user so that it persists in db
    newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
    usersRepository.save(newUser);
    eventPublisher.publishEvent(new UserRegistrationEvent(this, newUser));

    log.info("Registered user: {}", newUser);
    return responseBuilder(newUser);
  }

  public RegisterResponseDto responseBuilder(Users newUser) {
    RegisterResponseDto response = new RegisterResponseDto();
    response.setId(newUser.getId());
    response.setUsername(newUser.getUsername());
    response.setEmail(newUser.getEmail());
    response.setOwnedRefCode(newUser.getOwnedRefCode());
    if (newUser.getRefCodeUsed() != null) {
      response.setRefCodeUsed(
          "Successfully used referral code " + newUser.getRefCodeUsed() + " by user " + getUserByOwnedCode(
              newUser.getRefCodeUsed()).getUsername());
    } else {
      response.setRefCodeUsed("No referral code used");
    }
    response.setPointsWallet(new PointsWalletResponseDto(newUser.getPointsWallet()));
    response.setIsOrganizer(newUser.getIsOrganizer());
    response.setOrganizerWallet(newUser.getOrganizerWallet() == null ? null :
        new InitOrganizerWalletDto(newUser.getOrganizerWallet()));

    return response;
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
  public void update(ProfileUpdateRequestDTO requestDto) throws ImageNotFoundException {
    // get logged in user
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<Users> usersOptional = usersRepository.findByUsername(username);
    if (usersOptional.isPresent()) {
      Users existingUser = usersOptional.get();
      ProfileUpdateRequestDTO update = new ProfileUpdateRequestDTO();
      update.dtoToEntity(existingUser, requestDto);

      // check for image
      if (requestDto.getAvatarId() != null) {
        Image avatar = imageService.getImageById(requestDto.getAvatarId());
        if (avatar != null) {
          existingUser.setAvatar(avatar);
        } else {
          throw new ImageNotFoundException("Image doesn't exist in database. Please enter another imageId or upload a "
                                           + "new image");
        }
      }

      usersRepository.save(existingUser);
    }
  }


  // ref code related
  @Override
  public Users getUserByOwnedCode(String ownedCode) {
    return usersRepository.findByOwnedRefCode(ownedCode).orElse(null);
  }

  @Override
  public ReferralCodeUsageSummaryDto getCodeUsageSummary() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    // get user
    Users codeOwner = getByUsername(username);
    log.info("CodeOwner: {}", codeOwner);

    ReferralCodeUseCountDto owner = referralCodeUsageService.getReferralCodeUseCount(codeOwner);
    List<ReferralCodeUsersDto> usedBy = referralCodeUsageService.getReferralCodeUsers(codeOwner);

    return new ReferralCodeUsageSummaryDto(owner, usedBy);
  }

  @Override
  public Users getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    return usersRepository.findByUsername(username).orElse(null);
  }

  @SneakyThrows
  @Override
  public PointsWallet getUsersPointsWallet() {
    // get currently logged-in user
    Users currentUser = getCurrentUser();
    return pointsWalletService.getPointsWallet(currentUser);
  }

  @Override
  public Image uploadImage(ImageUploadRequestDto requestDto) {
    try {
      if (requestDto.getFileName().isEmpty()) {
        return null;
      }
      if (requestDto.getFile().isEmpty()) {
        return null;
      }

      // Get the currently logged-in user
      Users owner = getCurrentUser();

      // Define the objects
      String imageName = requestDto.getFileName();
      String imageUrl = cloudinaryService.uploadFile(requestDto.getFile(), "eventastic");

      Image image = new Image();
      image.setImageName(imageName);
      image.setImageUrl(imageUrl);
      if (image.getImageUrl() == null) {
        return null;
      }
      image.setOwner(owner);
      imageService.saveImage(image);

      return image;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Set<PointsTrx> getPointsTrx() {
    PointsWallet pointsWallet = getUsersPointsWallet();
    Set<PointsTrx> pointsTrxes = pointsTrxService.getPointsTrx(pointsWallet);
    if (pointsTrxes.isEmpty()) {
      throw new PointsTrxNotFoundException("No history of points usage is found!");
    } else {
      return pointsTrxes;
    }
  }

  @Override
  public OrganizerWalletDisplayDto getWalletDisplay() {
    Users organizer = getCurrentUser();
    OrganizerWallet organizerWallet;
    if (!organizer.getIsOrganizer()) {
      throw new AccessDeniedException("Only organizers can access this wallet");
    } else {
      organizerWallet = organizerWalletService.getWalletByOrganizer(organizer);
      if (organizerWallet == null) {
        throw new OrganizerWalletNotFound("Wallet not found! Or are you an impostor..");
      } else {
        return new OrganizerWalletDisplayDto(organizerWallet);
      }
    }
  }

}
