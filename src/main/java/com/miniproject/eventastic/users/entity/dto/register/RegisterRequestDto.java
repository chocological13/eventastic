package com.miniproject.eventastic.users.entity.dto.register;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
  @NotEmpty
  private String username;
  @NotNull
  @NotEmpty
  private String email;
  @NotNull
  @NotEmpty
  private String password;
  @NotNull
  @NotEmpty
  private String fullName;
  private String refCodeUsed;
  private Boolean isOrganizer;
}
