package com.miniproject.eventastic.auth.service;

import com.miniproject.eventastic.auth.entity.dto.forgorPassword.ForgotPasswordRequestDto;
import com.miniproject.eventastic.auth.entity.dto.forgorPassword.ForgotPasswordResponseDto;
import com.miniproject.eventastic.auth.entity.dto.resetPassword.ResetPasswordRequestDto;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import java.security.NoSuchAlgorithmException;

public interface ForgotPasswordService {

  ForgotPasswordResponseDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto)
      throws NoSuchAlgorithmException, JOSEException;

  String generateResetToken(String username) throws NoSuchAlgorithmException, JOSEException;

  Boolean resetPassword(String resetPasswordUrl, ResetPasswordRequestDto requestDto) throws Exception;
}
