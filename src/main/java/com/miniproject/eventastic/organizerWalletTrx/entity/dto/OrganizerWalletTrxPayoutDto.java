package com.miniproject.eventastic.organizerWalletTrx.entity.dto;

import com.miniproject.eventastic.organizerWalletTrx.entity.OrganizerWalletTrx;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;

@Data
public class OrganizerWalletTrxPayoutDto {

  private String payoutDescription;
  private Long payoutId;
  private Long trxId;
  private BigDecimal payoutAmount;
  private Instant creditedAt;
  private String disclaimer;

  public OrganizerWalletTrxPayoutDto(OrganizerWalletTrx organizerWalletTrx) {
    int percentage = organizerWalletTrx.getServiceFee().multiply(BigDecimal.valueOf(100)).intValue();
    this.payoutDescription = "Payout for Tickets purchased!";
    this.payoutId = organizerWalletTrx.getId();
    this.trxId = organizerWalletTrx.getTrx().getId();
    this.payoutAmount = organizerWalletTrx.getAmount();
    this.creditedAt = organizerWalletTrx.getCreatedAt();
    this.disclaimer = "We offer a secure and convenient payout system with a small" + percentage + "% service fee. "
                      + "The "
                      + "displayed "
                      + "amount reflects the final amount you will receive";
  }

  public OrganizerWalletTrxPayoutDto toDto(OrganizerWalletTrx organizerWalletTrx) {
    return new OrganizerWalletTrxPayoutDto(organizerWalletTrx);
  }


}
