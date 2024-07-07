package com.miniproject.eventastic.organizerWalletTrx.entity.dto;

import com.miniproject.eventastic.organizerWalletTrx.entity.OrganizerWalletTrx;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;

@Data
public class OrganizerWalletTrxDisplayDto {

  private Long payoutId;
  private String payoutDescription;
  private Long trxId;
  private BigDecimal payoutAmount;
  private Instant creditedAt;
  private String disclaimer;

  public OrganizerWalletTrxDisplayDto(OrganizerWalletTrx organizerWalletTrx) {
    int percentage =
        organizerWalletTrx.getServiceFee() != null ?
            organizerWalletTrx.getServiceFee().multiply(BigDecimal.valueOf(100)).intValue() : 0;
    String note = "We offer a secure and convenient payout system with a small " + percentage + "% service fee. The displayed amount reflects the final amount you will receive";
    this.payoutDescription = organizerWalletTrx.getDescription();
    this.payoutId = organizerWalletTrx.getId();
    this.trxId = organizerWalletTrx.getTrx().getId();
    this.payoutAmount = organizerWalletTrx.getAmount();
    this.creditedAt = organizerWalletTrx.getCreatedAt();
    this.disclaimer = percentage != 0 ? note : null;
  }

  public OrganizerWalletTrxDisplayDto toDto(OrganizerWalletTrx organizerWalletTrx) {
    return new OrganizerWalletTrxDisplayDto(organizerWalletTrx);
  }


}
