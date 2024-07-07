package com.miniproject.eventastic.organizerWallet.entity.dto;

import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.organizerWalletTrx.entity.dto.OrganizerWalletTrxDisplayDto;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class OrganizerWalletDisplayDto {

  private Long walletId;
  private String organizerName;
  private BigDecimal balance;
  private Set<OrganizerWalletTrxDisplayDto> historyTrxes;

  public OrganizerWalletDisplayDto(OrganizerWallet organizerWallet) {
    this.walletId = organizerWallet.getId();
    this.organizerName = organizerWallet.getOrganizer().getUsername();
    this.balance = organizerWallet.getBalance();
    this.historyTrxes = organizerWallet.getOrganizerWalletTrxes()
        .stream()
        .map(OrganizerWalletTrxDisplayDto::new)
        .collect(Collectors.toSet());
  }

  public OrganizerWalletDisplayDto toDto(OrganizerWallet organizerWallet) {
    return new OrganizerWalletDisplayDto(organizerWallet);
  }

}
