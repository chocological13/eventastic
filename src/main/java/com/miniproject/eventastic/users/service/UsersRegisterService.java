package com.miniproject.eventastic.users.service;

import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;

public interface UsersRegisterService {

  RegisterResponseDto register(RegisterRequestDto registerRequestDto);
}
