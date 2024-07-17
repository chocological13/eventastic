package com.miniproject.eventastic.users.service.impl;

import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.auth.entity.dto.changePassword.ChangePasswordRequestDto;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.exceptions.trx.OrganizerWalletNotFoundException;
import com.miniproject.eventastic.exceptions.trx.PointsTrxNotFoundException;
import com.miniproject.eventastic.exceptions.trx.PointsWalletNotFoundException;
import com.miniproject.eventastic.exceptions.user.DuplicateCredentialsException;
import com.miniproject.eventastic.exceptions.user.UserNotFoundException;
import com.miniproject.eventastic.image.entity.ImageUserAvatar;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.image.service.CloudinaryService;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.organizerWallet.entity.dto.OrganizerWalletDisplayDto;
import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageSummaryDto;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUseCountDto;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsersDto;
import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
  private final PointsWalletService pointsWalletService;
  private final CloudinaryService cloudinaryService;
  private final ImageService imageService;
  private final PointsTrxService pointsTrxService;
  private final OrganizerWalletService organizerWalletService;
  private final AttendeeService attendeeService;
  private final VoucherService voucherService;
  private final ModelMapper modelMapper;

  @Override
  public void saveUser(Users user) {
    usersRepository.save(user);
  }

  @Override
  public Users getCurrentUser() throws RuntimeException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new AccessDeniedException("You must be logged in to access this resource");
    }
    String username = authentication.getName();
    return getByUsername(username);
  }

  @Override
  @Transactional
  public UserProfileDto getProfile() throws RuntimeException {
    Users user = getCurrentUser();
    return new UserProfileDto(user);
  }

  @Override
  public Users getByUsername(String username) throws RuntimeException {
    Optional<Users> usersOptional = usersRepository.findByUsername(username);
    return usersOptional.orElseThrow(() -> new UserNotFoundException(
        "User by username: " + username + " not found. Please ensure you've entered the correct username!"));
  }

  @Override
  public void resetPassword(Users user, String newPassword) {
    user.setPassword(passwordEncoder.encode(newPassword));
    usersRepository.save(user);
  }

  @Override
  public void changePassword(ChangePasswordRequestDto requestDto) throws RuntimeException {
    Users loggedInUser = getCurrentUser();

    // Verify old password
    if (!passwordEncoder.matches(requestDto.getOldPassword(), loggedInUser.getPassword())) {
      throw new IllegalArgumentException("Old password is incorrect");
    }

    // Check if new password matches confirm password
    if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
      throw new IllegalArgumentException("New password and confirm password do not match");
    }

    // Check if new password is the same as the old password
    if (passwordEncoder.matches(requestDto.getNewPassword(), loggedInUser.getPassword())) {
      throw new IllegalArgumentException("New password cannot be the same as the old password");
    }

    // Update password
    loggedInUser.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
    usersRepository.save(loggedInUser);
  }

  @Override
  public void update(ProfileUpdateRequestDTO requestDto)
      throws RuntimeException {
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
      throws RuntimeException {
    // will throw UserNotFoundException, AccessDeniedException, ReferralCodeUnusedException
    Users codeOwner = getCurrentUser();
    log.info("CodeOwner: {}", codeOwner);

    ReferralCodeUseCountDto owner = referralCodeUsageService.getReferralCodeUseCount(codeOwner);
    List<ReferralCodeUsersDto> usedBy = referralCodeUsageService.getReferralCodeUsers(codeOwner);

    return new ReferralCodeUsageSummaryDto(owner, usedBy);
  }

  @SneakyThrows
  @Override
  public PointsWallet getUsersPointsWallet()
      throws RuntimeException {
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
    }
    return pointsTrxes;
  }

  @Override
  public OrganizerWalletDisplayDto getWalletDisplay() throws RuntimeException {
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
  public Page<EventResponseDto> getAttendeeEvents(int page, int size) throws RuntimeException {
    Users currentUser = getCurrentUser();
    Long userId = currentUser.getId();
    Pageable pageable = PageRequest.of(page, size);
    Page<Event> eventsPage = attendeeService.findEventsByAttendee(userId, pageable);
    return eventsPage.map(EventResponseDto::new);
  }

}
