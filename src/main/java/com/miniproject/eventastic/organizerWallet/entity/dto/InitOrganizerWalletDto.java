package com.miniproject.eventastic.organizerWallet.entity.dto;

import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InitOrganizerWalletDto {

  private Long id;
  private BigDecimal balance;

  public InitOrganizerWalletDto(OrganizerWallet organizerWallet) {
    this.id = organizerWallet.getId();
    this.balance = organizerWallet.getBalance();
  }

  public InitOrganizerWalletDto toDto(OrganizerWallet organizerWallet) {
    return new InitOrganizerWalletDto(organizerWallet);
  }
}
