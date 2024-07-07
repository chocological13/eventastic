package com.miniproject.eventastic.users.entity.dto.register;

import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
import com.miniproject.eventastic.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDto {
  private Long id;
  private String username;
  private String email;
  private String ownedRefCode;
  private String refCodeUsed;
  private PointsWalletResponseDto pointsWallet;

  public RegisterResponseDto(Users newUser) {
    this.id = newUser.getId();
    this.username = newUser.getUsername();
    this.email = newUser.getEmail();
    this.ownedRefCode = newUser.getOwnedRefCode();
    this.refCodeUsed = newUser.getRefCodeUsed();
    this.pointsWallet = new PointsWalletResponseDto(newUser.getPointsWallet());
  }

  public RegisterResponseDto toDto(Users newUser) {
    return new RegisterResponseDto(newUser);
  }
}
