package com.miniproject.eventastic.users.entity.dto.register;

import com.miniproject.eventastic.users.entity.Users;
import jakarta.validation.constraints.NotNull;
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

  @NotNull
  private String username;
  @NotNull
  private String email;
  @NotNull
  private String password;
  @NotNull
  private String fullName;
  private String refCodeUsed;
  private Boolean isOrganizer;

  public Users toEntity(Users user, RegisterRequestDto registerRequestDto) {
    user.setUsername(registerRequestDto.getUsername());
    user.setEmail(registerRequestDto.getEmail());
    user.setPassword(registerRequestDto.getPassword());
    user.setFullName(registerRequestDto.getFullName());
    Optional.ofNullable(registerRequestDto.refCodeUsed).ifPresent(user::setRefCodeUsed);
    user.setIsOrganizer(registerRequestDto.getIsOrganizer());
    return user;
  }
}
