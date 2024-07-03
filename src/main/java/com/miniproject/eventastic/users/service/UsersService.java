package com.miniproject.eventastic.users.service;

import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageSummaryDto;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import java.util.List;

public interface UsersService {

  List<UserProfileDto> getAllUsers();

  UserProfileDto getProfile();

  Users getByUsername(String username);

  Users getByEmail(String email);

  Users getById(Long id);

  RegisterResponseDto register(Users newUser, RegisterRequestDto requestDto);

  void resetPassword(Users user, String newPassword);

  void update(ProfileUpdateRequestDTO requestDto);

  // ref code related
  void saveRefCode(ReferralCodeUsage usage);

  ReferralCodeUsageSummaryDto getCodeUsageSummary();

  // getting logged-in user
  Users getCurrentUser();

  // showing user's points wallet
  PointsWalletResponseDto getUsersPointsWallet();

}
