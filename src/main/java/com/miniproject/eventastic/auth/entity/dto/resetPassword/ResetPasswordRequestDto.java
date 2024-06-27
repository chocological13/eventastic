package com.miniproject.eventastic.auth.entity.dto.resetPassword;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequestDto {

  @NotNull
  private String newPassword;
  @NotNull
  private String confirmPassword;
}
