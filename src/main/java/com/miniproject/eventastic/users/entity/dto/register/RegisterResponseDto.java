package com.miniproject.eventastic.users.entity.dto.register;

import com.miniproject.eventastic.organizerWallet.entity.dto.InitOrganizerWalletDto;
import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.trx.TrxVoucherResponseDto;
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
  private TrxVoucherResponseDto voucher;
  private PointsWalletResponseDto pointsWallet;
  private Boolean isOrganizer;
  private InitOrganizerWalletDto organizerWallet;

  public RegisterResponseDto(Users newUser, Voucher newVoucher) {
    this.id = newUser.getId();
    this.username = newUser.getUsername();
    this.email = newUser.getEmail();
    this.ownedRefCode = newUser.getOwnedRefCode();
    this.refCodeUsed = newUser.getRefCodeUsed() == null ?
        null : newUser.getRefCodeUsed().isEmpty() ?
        "No referral code used, no registration voucher assigned" :
        newUser.getRefCodeUsed();
    this.voucher = newVoucher == null ? null : new TrxVoucherResponseDto(newVoucher);
    this.pointsWallet = new PointsWalletResponseDto(newUser.getPointsWallet());
    this.isOrganizer = newUser.getIsOrganizer();
    this.organizerWallet = newUser.getOrganizerWallet() == null ? null :
        new InitOrganizerWalletDto(newUser.getOrganizerWallet());
  }

  public RegisterResponseDto toDto(Users newUser, Voucher newVoucher) {
    return new RegisterResponseDto(newUser, newVoucher);
  }
}
