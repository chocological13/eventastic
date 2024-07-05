package com.miniproject.eventastic.voucher.entity.dto.create;

import lombok.Data;

@Data
public class CreateVoucherRequestDto {
  private String code;
  private Long awardeeId;
  private Long eventId;
  private String description;
  private Integer percentDiscount;
  private Integer validity;
  private Integer useLimit;
}
