package com.miniproject.eventastic.auth.controller;

import com.miniproject.eventastic.auth.entity.dto.forgorPassword.ForgotPasswordRequestDto;
import com.miniproject.eventastic.auth.entity.dto.forgorPassword.ForgotPasswordResponseDto;
import com.miniproject.eventastic.auth.entity.dto.login.LoginRequestDto;
import com.miniproject.eventastic.auth.entity.dto.resetPassword.ResetPasswordRequestDto;
import com.miniproject.eventastic.auth.service.AuthService;
import com.miniproject.eventastic.auth.service.ForgotPasswordService;
import com.miniproject.eventastic.responses.Response;
import com.nimbusds.jose.JOSEException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Log
public class AuthController {

  private final AuthService authService;
  private final ForgotPasswordService forgotPasswordService;

  // > DEV: check who is currently logged in this session
  @GetMapping("")
  public String getLoggedInUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    String role = auth.getAuthorities().iterator().next().getAuthority();
    return "Logged in user: " + username + " with role: " + role;
  }

  // > login
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDto requestDto) {
    log.info("Login request for email: " + requestDto.getUsernameOrEmail());
    return authService.login(requestDto);
  }

  // > logout
  @GetMapping("/logout")
  public ResponseEntity<?> logout() {
    authService.logout();
    return ResponseEntity.ok().body(
        "Logout request for user: " + SecurityContextHolder.getContext().getAuthentication().getName() + " successful");
  }

  // > forgot password
  @PostMapping("/forgot-password")
  public ResponseEntity<Response<ForgotPasswordResponseDto>> forgotPassword(@RequestBody ForgotPasswordRequestDto req)
      throws NoSuchAlgorithmException, JOSEException {
    ForgotPasswordResponseDto responseDto = forgotPasswordService.forgotPassword(req);
    if (responseDto != null) {
      return Response.successfulResponse(HttpStatus.ACCEPTED.value(),
          "Reset password request for user with email " + req.getEmail() + " received!", responseDto);
    } else {
      return Response.successfulResponse(null);
    }
  }

  // ! TODO: THIS!!!!!!
  // > reset password
  @PutMapping("/reset-password")
  public ResponseEntity<Response<Void>> resetPassword(@RequestParam String token,
      @RequestBody ResetPasswordRequestDto req) throws Exception {
    forgotPasswordService.resetPassword(token, req);
      return Response.successfulResponse("Password reset successful! You can log back in with the new password now! "
          + "^^");
  }

}
