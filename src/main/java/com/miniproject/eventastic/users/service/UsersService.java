package com.miniproject.eventastic.users.service;

import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.image.entity.ImageUserAvatar;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.organizerWallet.entity.dto.OrganizerWalletDisplayDto;
import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageSummaryDto;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import java.util.Set;
import org.springframework.data.domain.Page;

public interface UsersService {

  // Region - User management

  void saveUser(Users user);

  RegisterResponseDto register(RegisterRequestDto requestDto);

  void resetPassword(Users user, String newPassword);

  void update(ProfileUpdateRequestDTO requestDto) throws ImageNotFoundException;

  UserProfileDto getProfile();

  // Getting Users

  Users getByUsername(String username);

  Users getById(Long id);

  // getting logged-in user
  Users getCurrentUser();

  // Region - other entity's calls

  Users getUserByOwnedCode(String ownedCode);

  ReferralCodeUsageSummaryDto getCodeUsageSummary();

  // showing user's points wallet
  PointsWallet getUsersPointsWallet();

  // uploading picture per user
  ImageUserAvatar uploadAvatar(ImageUploadRequestDto requestDto);

  // display history of points usage
  Set<PointsTrx> getPointsTrx();

  // display organizer's wallet and payout history
  OrganizerWalletDisplayDto getWalletDisplay();

  // display events that the user is going to attend or have attended
  Page<EventResponseDto> getAttendeeEvents(int page, int size);

}
