package com.miniproject.eventastic.voucher.entity.dto.trx;

import com.miniproject.eventastic.voucher.entity.Voucher;
import lombok.Data;

@Data
public class TrxVoucherResponseDto {

  private String voucherCode;
  private Integer voucherDiscount;

  public TrxVoucherResponseDto(Voucher voucher) {
    this.voucherCode = voucher.getCode();
    this.voucherDiscount = voucher.getPercentDiscount();
  }

  public TrxVoucherResponseDto toDto(Voucher voucher) {
    return new TrxVoucherResponseDto(voucher);
  }

}
