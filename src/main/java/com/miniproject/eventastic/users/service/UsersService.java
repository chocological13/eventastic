package com.miniproject.eventastic.users.service;

import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import java.util.List;

public interface UsersService {

  List<Users> getAllUsers();

  UserProfileDto getProfile();

  Users getByUsername(String username);

  Users getByEmail(String email);

  Users getById(Long id);

  void register(Users newUser, RegisterRequestDto requestDto);

  void resetPassword(Users user, String newPassword);

  void update(ProfileUpdateRequestDTO requestDto);

}
