package com.miniproject.eventastic.trx.entity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrxPurchaseRequestDto {

  @NotNull(message = "Must choose an event!")
  private Long eventId;
  @NotNull(message = "Must choose a ticket type")
  private Long ticketTypeId;
  @Min(value = 1)
  private int qty;
  @NotNull(message = "Must choose a payment method")
  private Long paymentId;

  // nullable
  private String voucherCode;
  private Boolean usingPoints; // get this from logged-in user's points wallet

}
