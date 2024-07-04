package com.miniproject.eventastic.users.service;

import com.miniproject.eventastic.exceptions.ImageNotFoundException;
import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageSummaryDto;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UsersService {

  void saveUser(Users user);

  void resetPassword(Users user, String newPassword);

  void update(ProfileUpdateRequestDTO requestDto) throws ImageNotFoundException;

  List<UserProfileDto> getAllUsers();

  UserProfileDto getProfile();

  Users getByUsername(String username);

  Users getById(Long id);

  RegisterResponseDto register(RegisterRequestDto requestDto);

  // ref code related
  void saveRefCode(ReferralCodeUsage usage);

  Users getUserByOwnedCode(String ownedCode);

  ReferralCodeUsageSummaryDto getCodeUsageSummary();

  // getting logged-in user
  Users getCurrentUser();

  // showing user's points wallet
  PointsWalletResponseDto getUsersPointsWallet();

  // uploading picture per user
  Image uploadImage(ImageUploadRequestDto requestDto);


}
