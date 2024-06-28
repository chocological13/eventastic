package com.miniproject.eventastic.users.entity.dto.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDto {

  private String welcomeMessage;
  private String username;
  private String email;
  private String fullName;
  private String refCodeUsed;
}
