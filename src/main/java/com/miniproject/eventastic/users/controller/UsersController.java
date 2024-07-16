package com.miniproject.eventastic.users.controller;

import com.miniproject.eventastic.auth.entity.dto.changePassword.ChangePasswordRequestDto;
import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.exceptions.trx.OrganizerWalletNotFoundException;
import com.miniproject.eventastic.exceptions.trx.PointsTrxNotFoundException;
import com.miniproject.eventastic.exceptions.trx.TicketNotFoundException;
import com.miniproject.eventastic.image.entity.ImageUserAvatar;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.image.entity.dto.ImageUploadResponseDto;
import com.miniproject.eventastic.organizerWallet.entity.dto.OrganizerWalletDisplayDto;
import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsTrx.entity.dto.PointsTrxDto;
import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageSummaryDto;
import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.ticket.entity.dto.TrxIssuedTicketDto;
import com.miniproject.eventastic.trx.service.TrxService;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.service.UsersRegisterService;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.users.service.impl.UsersRegisterServiceImpl;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.validation.Valid;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class UsersController {

  private final UsersService usersService;
  private final VoucherService voucherService;
  private final TrxService trxService;
  private final UsersRegisterService usersRegisterService;

  // * Register
  @PostMapping("/register")
  public ResponseEntity<Response<RegisterResponseDto>> registerUser(@Valid @RequestBody RegisterRequestDto requestDto) {
      RegisterResponseDto response = usersRegisterService.register(requestDto);
      return Response.successfulResponse(HttpStatus.CREATED.value(),
          "Register successful!!",
          response);
  }

  // * Get logged in user's profile
  @GetMapping("/me")
  public ResponseEntity<Response<UserProfileDto>> getUserProfile() {
    UserProfileDto userProfile = usersService.getProfile();
    if (userProfile != null) {
      return Response.successfulResponse(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), userProfile);
    } else {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), "There is no user profile", null);
    }
  }

  // * Get logged-in user's points wallet
  @GetMapping("/me/points")
  public ResponseEntity<Response<PointsWalletResponseDto>> getUsersPointsWallet() {
    String currentUser =
        usersService.getCurrentUser().getFullName();
    return Response.successfulResponse(HttpStatus.OK.value(), "Showing Points Wallet for: " + currentUser,
        new PointsWalletResponseDto(usersService.getUsersPointsWallet()));
  }

  @GetMapping("/me/points/history")
  public ResponseEntity<Response<Set<PointsTrxDto>>> getPointsUsageHistory() {
    Set<PointsTrxDto> pointsTrxDtos = new LinkedHashSet<>();
    try {
      Set<PointsTrx> pointsTrxes = usersService.getPointsTrx();
      pointsTrxDtos = pointsTrxes.stream().map(PointsTrxDto::new).collect(Collectors.toSet());
    } catch (PointsTrxNotFoundException e) {
      Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
    return Response.successfulResponse(HttpStatus.OK.value(), "Displaying points usage history..", pointsTrxDtos);
  }

  // TODO : Get logged-in user's vouchers
//  @GetMapping("/me/vouchers")
//  public ResponseEntity<Response<List<CreateEventVoucherResponseDto>>> getAwardeesVoucher() {
//    try {
//      List<Voucher> voucherList = voucherService.getAwardeesVouchers();
//      List<CreateEventVoucherResponseDto> responseDtos = voucherList.stream()
//          .map(CreateEventVoucherResponseDto::new)
//          .toList();
//      return Response.successfulResponse(HttpStatus.OK.value(), "Displaying your vouchers..", responseDtos);
//    } catch (VoucherNotFoundException e) {
//      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
//    }
//  }

  // * Edit Profile
  @PutMapping("/me/update")
  public ResponseEntity<Response<UserProfileDto>> updateUserProfile(@Valid @RequestBody ProfileUpdateRequestDTO requestDTO)
      throws ImageNotFoundException {
    usersService.update(requestDTO);
    UserProfileDto userProfile = usersService.getProfile();
    return Response.successfulResponse(HttpStatus.OK.value(), "Profile update successful!! :D", userProfile);
  }

  @PutMapping("me/change-password")
  public ResponseEntity<Response<Void>> changePassword(@Valid @RequestBody ChangePasswordRequestDto requestDto) {
    usersService.changePassword(requestDto);
    return Response.successfulResponse("Password change successful!");
  }

  // * Ref Code related
  @GetMapping("/me/referral/usage")
  public ReferralCodeUsageSummaryDto referralCodeUsageSummary() {
    return usersService.getCodeUsageSummary();
  }

  // * upload image
  @PostMapping("/me/image/upload")
  public ResponseEntity<Response<ImageUploadResponseDto>> uploadImage(ImageUploadRequestDto requestDto) {
    ImageUserAvatar uploadedImageUserAvatar = usersService.uploadAvatar(requestDto);
    if (uploadedImageUserAvatar == null) {
      return ResponseEntity.noContent().build();
    } else {
      return Response.successfulResponse(HttpStatus.OK.value(), "Image uploaded! :D", new ImageUploadResponseDto(
          uploadedImageUserAvatar));
    }
  }

  // * show list of purchased tickets
  @GetMapping("/me/tickets")
  public ResponseEntity<Response<Set<TrxIssuedTicketDto>>> getPurchasedTickets() {
    try {
      Set<Ticket> ticketSet = trxService.getUserTickets();
      return Response.successfulResponse(HttpStatus.OK.value(), "Displaying your tickets..",
          ticketSet.stream().map(TrxIssuedTicketDto::new).collect(Collectors.toSet()));
    } catch (TicketNotFoundException e) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
  }

  // * show list of events the user is an attendee to
  @GetMapping("/me/events")
  public ResponseEntity<Response<Map<String, Object>>> getAttendeeEvent(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return Response.responseMapper(HttpStatus.OK.value(), "Displaying events you are an attendee to..",
        usersService.getAttendeeEvents(page, size));
  }

  // !! Organizer space
  @GetMapping("/organizer/wallet")
  public ResponseEntity<Response<OrganizerWalletDisplayDto>> getOrganizerWallet() {
    try {
      return Response.successfulResponse(HttpStatus.OK.value(), "Displaying your wallet..",
          usersService.getWalletDisplay());
    } catch (AccessDeniedException e) {
      return Response.failedResponse(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
    } catch (OrganizerWalletNotFoundException e) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
  }

}
