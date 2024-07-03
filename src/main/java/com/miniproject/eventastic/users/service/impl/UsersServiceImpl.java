package com.miniproject.eventastic.users.service.impl;

import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
import com.miniproject.eventastic.pointsWallet.service.impl.PointsWalletServiceImpl;
import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageSummaryDto;
import com.miniproject.eventastic.referralCodeUsage.repository.ReferralCodeUsageRepository;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.event.UserRegistrationEvent;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
  private final ReferralCodeUsageRepository referralCodeUsageRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final PointsWalletServiceImpl pointsWalletService;

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
  public Users getByEmail(String email) {
    Optional<Users> usersOptional = usersRepository.findByEmail(email);
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
    response.setWelcomeMessage("Welcome to Eventastic, " + newUser.getFirstName() + " " + newUser.getLastName());
    response.setId(newUser.getId());
    response.setUsername(newUser.getUsername());
    response.setEmail(newUser.getEmail());
    response.setOwnedRefCode(newUser.getOwnedRefCode());
    response.setRefCodeUsed(newUser.getRefCodeUsed());
    response.setPointsWallet(new PointsWalletResponseDto(newUser.getPointsWallet()));
    return response;
  }

  @Override
  public void resetPassword(Users user, String newPassword) {
    user.setPassword(passwordEncoder.encode(newPassword));
    usersRepository.save(user);
  }

  @Override
  public void update(ProfileUpdateRequestDTO requestDto) {
    // get logged in user
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<Users> usersOptional = usersRepository.findByUsername(username);
    if (usersOptional.isPresent()) {
      Users existingUser = usersOptional.get();
      ProfileUpdateRequestDTO update = new ProfileUpdateRequestDTO();
      update.profileUpdateRequestDTOtoUsers(existingUser, requestDto);
      usersRepository.save(existingUser);
    }
  }


  // refcode related
  @Override
  public void saveRefCode(ReferralCodeUsage usage) {
    referralCodeUsageRepository.save(usage);
  }

  @Override
  public ReferralCodeUsageSummaryDto getCodeUsageSummary() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    // get user
    Users codeOwner = getByUsername(username);
    log.info("CodeOwner: {}", codeOwner);

    ReferralCodeUsageSummaryDto response = new ReferralCodeUsageSummaryDto();
    response.setReferralCodeUsageOwnerDto(referralCodeUsageRepository.findUsageSummaryByCodeOwner(codeOwner));
    response.setReferralCodeUsageByDto(referralCodeUsageRepository.findUsersByCodeOwner(codeOwner));
    return response;
  }

  @Override
  public Users getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    return usersRepository.findByUsername(username).orElse(null);
  }

  @SneakyThrows
  @Override
  public PointsWalletResponseDto getUsersPointsWallet() {
    // get currently logged-in user
    Users currentUser = getCurrentUser();
    return pointsWalletService.getPointsWallet(currentUser);
  }

}
