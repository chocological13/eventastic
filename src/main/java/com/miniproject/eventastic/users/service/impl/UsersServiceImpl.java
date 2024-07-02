package com.miniproject.eventastic.users.service.impl;

import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.composite.ReferralCodeUsageId;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageSummaryDto;
import com.miniproject.eventastic.referralCodeUsage.repository.ReferralCodeUsageRepository;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
  private final ReferralCodeUsageRepository referralCodeUsageRepository;

  @Override
  public List<Users> getAllUsers() {
    return usersRepository.findAll();
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
  public RegisterResponseDto register(Users newUser, RegisterRequestDto requestDto) {
    // init
    RegisterResponseDto response = new RegisterResponseDto();
    RegisterRequestDto reqToUser = new RegisterRequestDto();

    // map to entity
    reqToUser.toEntity(newUser, requestDto);
    newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
    usersRepository.save(newUser);

    //* create ref code
    String ownedReferralCode = UUID.randomUUID().toString().substring(0, 7);
    newUser.setOwnedRefCode(ownedReferralCode);
    response.setOwnedRefCode(ownedReferralCode);

    //* check if user entered other user's ref code
    String refCodeUsed = requestDto.getRefCodeUsed();

    //* look for owner of code
    Optional<Users> ownerOptional = usersRepository.findByOwnedRefCode(refCodeUsed);
    log.info("RefCodeUsed: {}, Owner: {}", refCodeUsed, ownerOptional.orElse(null));
    if (ownerOptional.isPresent()) {
      Users owner = ownerOptional.get();

      // ! TODO: add business logic for giving voucher to new user and points to owner of code here

      // > Log the usage of referral code
      ReferralCodeUsage usage = new ReferralCodeUsage(
          new ReferralCodeUsageId(newUser.getId(), owner.getId()),
          newUser,
          owner,
          Instant.now()
      );
      saveRefCode(usage);
      response.setRefCodeUsed("Referral code from user " + owner.getUsername() + " was used!");
    } else {
      response.setRefCodeUsed("No referral code was used");
    }
    String fullName = newUser.getFirstName() + " " + newUser.getLastName();
    response.setWelcomeMessage("Welcome to Eventastic, " + fullName);
    response.setUsername(newUser.getUsername());
    response.setEmail(newUser.getEmail());
    response.setFullName(fullName);
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

}
