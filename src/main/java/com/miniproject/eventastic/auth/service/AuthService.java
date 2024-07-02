package com.miniproject.eventastic.auth.service;

import com.miniproject.eventastic.auth.entity.dto.login.LoginRequestDto;
import com.miniproject.eventastic.users.entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {

 String generateToken(Authentication authentication);

 ResponseEntity<?> login(LoginRequestDto loginRequestDto);

 void logout();

}
