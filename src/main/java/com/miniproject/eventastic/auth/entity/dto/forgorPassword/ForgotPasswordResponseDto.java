package com.miniproject.eventastic.auth.entity.dto.forgorPassword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordResponseDto {

  private String message;
  private String resetTokenUrl;

}
