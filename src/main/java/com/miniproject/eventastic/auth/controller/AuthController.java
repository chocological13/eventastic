package com.miniproject.eventastic.auth.controller;

import com.miniproject.eventastic.auth.entity.UserAuth;
import com.miniproject.eventastic.auth.entity.dto.login.LoginRequestDto;
import com.miniproject.eventastic.auth.entity.dto.login.LoginResponseDto;
import com.miniproject.eventastic.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

  private final AuthenticationManager authenticationManager;
  private final AuthService authService;

  // > DEV: check who is currently logged in this session
  @GetMapping("")
  public Object getLoggedInUser() {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
  }

  // > login
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDto requestDto) {
    log.info("Login request for user: " + requestDto.getUsername());

    // * 1: authenticate user
    Authentication auth = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword()));

    // * 2: store it in the security context
    SecurityContextHolder.getContext().setAuthentication(auth);
    var ctx = SecurityContextHolder.getContext();
    ctx.setAuthentication(auth);

    // * 3: get user's information
    UserAuth userDetails = (UserAuth) auth.getPrincipal();
    log.info("User logged in: " + userDetails.getUsername() + " with roles: " + userDetails.getAuthorities());

    // ! 4: generate token
    String token = authService.generateToken(auth);

    // * 5: generate response
    LoginResponseDto response = new LoginResponseDto();
    response.setMessage("Login request successful");
    response.setToken(token);

    // * 6: create cookie
    ResponseCookie cookie =ResponseCookie.from("token", token)
        .path("/")
        .httpOnly(true)
        .maxAge(3600)
        .build();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Set-Cookie", cookie.toString());

    // * 7: return the token
    return ResponseEntity.ok().headers(headers).body(response);
  }

}
