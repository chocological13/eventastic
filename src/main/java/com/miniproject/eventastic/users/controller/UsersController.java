package com.miniproject.eventastic.users.controller;

import com.miniproject.eventastic.exceptions.ImageNotFoundException;
import com.miniproject.eventastic.exceptions.VoucherNotFoundException;
import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.image.entity.dto.ImageUploadResponseDto;
import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageSummaryDto;
import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.VoucherResponseDto;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class UsersController {

  private final UsersService usersService;
  private final VoucherService voucherService;

  // ! Get all
  @GetMapping
  public ResponseEntity<Response<List<UserProfileDto>>> getAllUsers() {
    try {
      List<UserProfileDto> users = usersService.getAllUsers();
      return Response.successfulResponse(HttpStatus.FOUND.value(), "Displaying all users..", users);
    } catch (EmptyResultDataAccessException e) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
  }

  // * Register
  @PostMapping("/register")
  public ResponseEntity<Response<RegisterResponseDto>> registerUser(@Valid @RequestBody RegisterRequestDto requestDto) {
    RegisterResponseDto response = usersService.register(requestDto);
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

  // * Get logged-in user's points wallet
  @GetMapping("/points")
  public ResponseEntity<Response<PointsWalletResponseDto>> getUsersPointsWallet() {
    String currentUser =
        usersService.getCurrentUser().getFullName();
    return Response.successfulResponse(HttpStatus.FOUND.value(), "Showing Points Wallet for: " + currentUser,
        usersService.getUsersPointsWallet());
  }

  // * Get logged-in user's vouchers
  @GetMapping("/vouchers")
  public ResponseEntity<Response<List<VoucherResponseDto>>> getAwardeesVoucher() {
    try {
      List<Voucher> voucherList = voucherService.getAwardeesVouchers();
      List<VoucherResponseDto> responseDtos = voucherList.stream()
          .map(VoucherResponseDto::new)
          .toList();
      return Response.successfulResponse(HttpStatus.FOUND.value(), "Displaying your vouchers..", responseDtos);
    } catch (VoucherNotFoundException e) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
  }

  // * Edit Profile
  @PutMapping("/me/update")
  public ResponseEntity<Response<UserProfileDto>> updateUserProfile(@Valid @RequestBody ProfileUpdateRequestDTO requestDTO)
      throws ImageNotFoundException {
    usersService.update(requestDTO);
    UserProfileDto userProfile = usersService.getProfile();
    return Response.successfulResponse(HttpStatus.OK.value(), "Profile update successful!! :D", userProfile);
  }

  // * Ref Code related
  @GetMapping("/referral/usage")
  public ReferralCodeUsageSummaryDto referralCodeUsageSummary() {
    return usersService.getCodeUsageSummary();
  }

  // * upload image
  @PostMapping("/image/upload")
  public ResponseEntity<Response<ImageUploadResponseDto>> uploadImage(ImageUploadRequestDto requestDto) {
    Image uploadedImage = usersService.uploadImage(requestDto);
    if (uploadedImage == null) {
      return ResponseEntity.noContent().build();
    } else {
      return Response.successfulResponse(HttpStatus.OK.value(), "Image uploaded! :D", new ImageUploadResponseDto(uploadedImage));
    }
  }
}
