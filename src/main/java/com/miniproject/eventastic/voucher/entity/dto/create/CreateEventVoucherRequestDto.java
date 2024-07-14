package com.miniproject.eventastic.voucher.entity.dto.create;

import lombok.Data;

@Data
public class CreateEventVoucherRequestDto {
  private String code;
  private String description;
  private Integer percentDiscount;
  private Integer validity;
  private Integer useLimit;
}
