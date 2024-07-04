package com.miniproject.eventastic.referralCodeUsage.entity.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReferralCodeUsersDto {

  private Long usedById;
  private String usedByUsername;
  private Instant usedAt;

  public ReferralCodeUsersDto(Long usedById, String usedByUsername, Instant usedAt) {
    this.usedById = usedById;
    this.usedByUsername = usedByUsername;
    this.usedAt = usedAt;
  }

  public ReferralCodeUsersDto toDto(Long usedById, String usedByUsername, Instant usedAt) {
    return new ReferralCodeUsersDto(usedById, usedByUsername, usedAt);
  }

}
