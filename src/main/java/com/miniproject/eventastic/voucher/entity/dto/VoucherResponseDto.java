package com.miniproject.eventastic.voucher.entity.dto;

import com.miniproject.eventastic.voucher.entity.Voucher;
import java.time.Instant;
import lombok.Data;

@Data
public class VoucherResponseDto {

  private Long id;
  private String code;
  private String description;
  private String awardedTo;
  private String eventName;
  private Integer discountPercentage;
  private Instant createdAt;
  private Instant expiresAt;

  public VoucherResponseDto(Voucher voucher) {

    this.id = voucher.getId();
    this.code = voucher.getCode();
    this.description = voucher.getDescription();
    this.awardedTo = voucher.getAwardedTo() != null ?
        voucher.getAwardedTo().getUsername() :
        "Available for all users!";
    this.eventName = voucher.getEvent() != null ?
        voucher.getEvent().getTitle() :
        "Available for all events!";
    this.discountPercentage = voucher.getDiscountPercentage();
    this.createdAt = voucher.getCreatedAt();
    this.expiresAt = voucher.getExpiresAt();
  }

  public VoucherResponseDto toDto(Voucher voucher) {
    return new VoucherResponseDto(voucher);
  }

}
