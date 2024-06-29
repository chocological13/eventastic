package com.miniproject.eventastic.referralCodeUsage.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferralCodeUsageSummaryDto {

  private ReferralCodeUsageOwnerDto referralCodeUsageOwnerDto;
  private ReferralCodeUsageByDto referralCodeUsageByDto;

}
