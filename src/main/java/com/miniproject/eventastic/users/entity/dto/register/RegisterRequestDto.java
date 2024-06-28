package com.miniproject.eventastic.users.entity.dto.register;

import com.miniproject.eventastic.users.entity.Users;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

  private String username;
  private String email;
  private String password;
  private String firstName;
  private String lastName;
  private String refCodeUsed;
  private Boolean isOrganizer;

  public Users toEntity(Users user, RegisterRequestDto registerRequestDto) {
    user.setUsername(registerRequestDto.getUsername());
    user.setEmail(registerRequestDto.getEmail());
    user.setPassword(registerRequestDto.getPassword());
    user.setFirstName(registerRequestDto.getFirstName());
    user.setLastName(registerRequestDto.getLastName());
    Optional.ofNullable(registerRequestDto.refCodeUsed).ifPresent(user::setRefCodeUsed);
    user.setIsOrganizer(registerRequestDto.getIsOrganizer());
    return user;
  }
}
