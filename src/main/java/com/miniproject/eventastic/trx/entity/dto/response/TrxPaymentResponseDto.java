package com.miniproject.eventastic.trx.entity.dto.response;

import com.miniproject.eventastic.trx.entity.Payment;
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
