package com.miniproject.eventastic.pointsWallet.entity.dto;

import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import java.time.Instant;
import lombok.Data;

@Data
public class PointsWalletResponseDto {

  private Long id;
  private Integer points;
  private Instant awardedAt;
  private Instant expiresAt;

  public PointsWalletResponseDto(PointsWallet pointsWallet) {
    this.id = pointsWallet.getId();
    this.points = pointsWallet.getPoints();
    this.awardedAt = pointsWallet.getAwardedAt();
    this.expiresAt = pointsWallet.getExpiresAt();
  }

  public PointsWalletResponseDto toDto(PointsWallet pointsWallet) {
    return new PointsWalletResponseDto(pointsWallet);
  }


}
