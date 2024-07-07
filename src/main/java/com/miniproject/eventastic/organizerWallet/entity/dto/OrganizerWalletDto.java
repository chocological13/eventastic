package com.miniproject.eventastic.organizerWallet.entity.dto;

import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganizerWalletDto {

  private Long id;
  private BigDecimal balance;

  public OrganizerWalletDto(OrganizerWallet organizerWallet) {
    this.id = organizerWallet.getId();
    this.balance = organizerWallet.getBalance();
  }

  public OrganizerWalletDto toDto(OrganizerWallet organizerWallet) {
    return new OrganizerWalletDto(organizerWallet);
  }
}
