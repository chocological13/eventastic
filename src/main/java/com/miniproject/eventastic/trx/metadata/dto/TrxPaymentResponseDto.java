package com.miniproject.eventastic.trx.metadata.dto;

import com.miniproject.eventastic.trx.metadata.Payment;
import lombok.Data;

@Data
public class TrxPaymentResponseDto {

  private String bankName;
  private String bankAccountNumber;

  public TrxPaymentResponseDto(Payment payment) {
    this.bankName = payment.getBankName();
    this.bankAccountNumber = payment.getAccountNumber();
  }
}
