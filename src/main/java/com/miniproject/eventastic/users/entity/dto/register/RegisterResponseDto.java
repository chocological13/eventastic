package com.miniproject.eventastic.users.entity.dto.register;

import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
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
  private String ownedRefCode;
  private String refCodeUsed;
  private PointsWalletResponseDto pointsWallet;
}
