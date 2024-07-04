package com.miniproject.eventastic.referralCodeUsage.entity.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReferralCodeUsageSummaryDto {

  private ReferralCodeUseCountDto referralCodeUseCountDto;
  private List<ReferralCodeUsersDto> referralCodeUsersList;

  public ReferralCodeUsageSummaryDto(ReferralCodeUseCountDto owner, List<ReferralCodeUsersDto> usedBy) {
    this.referralCodeUseCountDto = owner;
    this.referralCodeUsersList = usedBy;
  }

  public ReferralCodeUsageSummaryDto getSummary(ReferralCodeUseCountDto owner,
      List<ReferralCodeUsersDto> usedBy) {
    return new ReferralCodeUsageSummaryDto(owner, usedBy);
  }

}
