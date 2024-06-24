package com.miniproject.eventastic.auth.service;

import org.springframework.security.core.Authentication;

public interface AuthService {

 public String generateToken(Authentication authentication);

}
