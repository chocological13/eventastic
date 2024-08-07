package com.miniproject.eventastic.auth.entity.dto.login;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

  @NotNull
  private String usernameOrEmail;

  @NotNull
  private String password;
}
