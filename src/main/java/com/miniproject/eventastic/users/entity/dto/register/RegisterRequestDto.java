package com.miniproject.eventastic.users.entity.dto.register;

import com.miniproject.eventastic.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

  private String username;
  private String email;
  private String password;
  private Boolean isOrganizer;

  public Users toEntity(Users user, RegisterRequestDto registerRequestDto) {
    user.setUsername(registerRequestDto.getUsername());
    user.setEmail(registerRequestDto.getEmail());
    user.setPassword(registerRequestDto.getPassword());
    user.setIsOrganizer(registerRequestDto.getIsOrganizer());
    return user;
  }
}
