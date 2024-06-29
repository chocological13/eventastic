package com.miniproject.eventastic.referralCodeUsage.entity.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferralCodeUsageByDto {

  private Long usedById;
  private String usedByUsername;
  private Instant usedAt;

}
