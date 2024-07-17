package com.miniproject.eventastic.voucher.entity.dto.create;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEventVoucherRequestDto {
  @NotEmpty
  private String code;
  private String description;
  @NotNull
  private Integer percentDiscount;
  @NotNull
  private Integer validity;
  @NotNull
  private Integer useLimit;
}
