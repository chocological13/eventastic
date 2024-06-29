package com.miniproject.eventastic.users.controller;

import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageOwnerDto;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageSummaryDto;
import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.service.UsersService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class UsersController {

  private final UsersService usersService;

  // ! Get all
  @GetMapping
  public ResponseEntity<Response<List<Users>>> getAllUsers() {
    List<Users> users = usersService.getAllUsers();
    if (!users.isEmpty()) {
      return Response.successfulResponse(HttpStatus.FOUND.value(), "Displaying all users...", users);
    } else {
      return Response.failedResponse("There are no users to display");
    }
  }

  // * Register
  @PostMapping("/register")
  public ResponseEntity<Response<RegisterResponseDto>> registerUser(@RequestBody RegisterRequestDto requestDto) {
    Users newUser = new Users();
    log.info("Registered user: {}", newUser);
    RegisterResponseDto response = usersService.register(newUser, requestDto);
    return Response.successfulResponse(HttpStatus.CREATED.value(),
        "Register successful!!",
        response);
  }

  // * Get logged in user's profile
  @GetMapping("/me")
  public ResponseEntity<Response<UserProfileDto>> getUserProfile() {
    UserProfileDto userProfile = usersService.getProfile();
    if (userProfile != null) {
      return Response.successfulResponse(HttpStatus.FOUND.value(), HttpStatus.FOUND.getReasonPhrase(), userProfile);
    } else {
      return Response.failedResponse("There is no user profile");
    }
  }

  // * Edit Profile
  @PostMapping("/me/update")
  public ResponseEntity<Response<UserProfileDto>> updateUserProfile(@RequestBody ProfileUpdateRequestDTO requestDTO) {
    usersService.update(requestDTO);
    UserProfileDto userProfile = usersService.getProfile();
    return Response.successfulResponse(HttpStatus.OK.value(), "Profile update successful!! :D", userProfile);
  }

  // > Ref Code related
  @GetMapping("/referral/usage")
  public ReferralCodeUsageSummaryDto referralCodeUsageSummary() {
    return usersService.getCodeUsageSummary();
  }
}
