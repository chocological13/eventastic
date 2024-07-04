package com.miniproject.eventastic.referralCodeUsage.entity.dto;

import lombok.Data;

@Data
public class ReferralCodeUseCountDto {

  private String codeOwner;
  private Long useCount;

  public ReferralCodeUseCountDto(String codeOwner, Long useCount) {
    this.codeOwner = codeOwner;
    this.useCount = useCount;
  }

  public ReferralCodeUseCountDto summary(String codeOwner, Long useCount) {
    return new ReferralCodeUseCountDto(codeOwner, useCount);
  }

}
