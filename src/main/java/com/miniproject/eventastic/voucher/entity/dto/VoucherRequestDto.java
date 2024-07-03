package com.miniproject.eventastic.voucher.entity.dto;

import lombok.Data;

@Data
public class VoucherRequestDto {
  private String code;
  private String description;
  private Integer discountPercentage;
  private Long awardedToId;
  private Long eventId;
}
