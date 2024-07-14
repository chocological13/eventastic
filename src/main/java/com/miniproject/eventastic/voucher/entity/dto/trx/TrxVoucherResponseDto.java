package com.miniproject.eventastic.voucher.entity.dto.trx;

import com.miniproject.eventastic.voucher.entity.Voucher;
import java.time.Instant;
import lombok.Data;

@Data
public class TrxVoucherResponseDto {

  private Long id;
  private String description;
  private String voucherCode;
  private Integer voucherPercent;
  private Instant expiresAt;

  public TrxVoucherResponseDto(Voucher voucher) {
    this.id = voucher.getId();
    this.description = voucher.getDescription();
    this.voucherCode = voucher.getCode();
    this.voucherPercent = voucher.getPercentDiscount();
    this.expiresAt = voucher.getExpiresAt();
  }

  public TrxVoucherResponseDto toDto(Voucher voucher) {
    return new TrxVoucherResponseDto(voucher);
  }

}
