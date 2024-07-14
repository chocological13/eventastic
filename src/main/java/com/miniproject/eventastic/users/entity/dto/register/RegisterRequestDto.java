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
}
