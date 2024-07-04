package com.miniproject.eventastic.referralCodeUsage.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReferralCodeUsageSummaryDto {

  private ReferralCodeUseCountDto referralCodeUseCountDto;
  private ReferralCodeUsersDto referralCodeUsersDto;

  public ReferralCodeUsageSummaryDto(ReferralCodeUseCountDto owner, ReferralCodeUsersDto usedBy) {
    this.referralCodeUseCountDto = owner;
    this.referralCodeUsersDto = usedBy;
  }

  public ReferralCodeUsageSummaryDto getSummary(ReferralCodeUseCountDto owner,
      ReferralCodeUsersDto usedBy) {
    return new ReferralCodeUsageSummaryDto(owner, usedBy);
  }

}
