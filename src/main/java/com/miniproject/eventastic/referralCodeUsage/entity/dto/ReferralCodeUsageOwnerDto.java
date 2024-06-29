package com.miniproject.eventastic.referralCodeUsage.entity.dto;

import lombok.Data;

@Data
public class ReferralCodeUsageOwnerDto {

  private String codeOwner;
  private Long useCount;

  public ReferralCodeUsageOwnerDto(String codeOwner, Long useCount) {
    this.codeOwner = codeOwner;
    this.useCount = useCount;
  }

}
