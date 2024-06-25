package com.miniproject.eventastic.auth.controller;

import com.miniproject.eventastic.auth.entity.dto.login.LoginRequestDto;
import com.miniproject.eventastic.auth.repository.AuthRedisRepository;
import com.miniproject.eventastic.auth.service.AuthService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Log
public class AuthController {

  private final AuthService authService;
  private final UsersService usersService;

  // > DEV: check who is currently logged in this session
  @GetMapping("")
  public Users getLoggedInUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    return usersService.getByUsername(username);
  }

  // > login
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDto requestDto) {
    log.info("Login request for user: " + requestDto.getUsername());
    return authService.login(requestDto);
  }

  // > logout
  @GetMapping("/logout")
  public ResponseEntity<?> logout() {
    authService.logout();
    return ResponseEntity.ok().body(
        "Logout request for user: " + SecurityContextHolder.getContext().getAuthentication().getName() + " successful");
  }

}
