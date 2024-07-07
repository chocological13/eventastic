package com.miniproject.eventastic.pointsTrx.entity.dto;

import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import java.time.Instant;
import lombok.Data;

@Data
public class PointsTrxDto {

  private Long id;
  private Integer points;
  private String trxType;
  private String description;
  private Instant trxDate;

  public PointsTrxDto(PointsTrx pointsTrx) {
    this.id = pointsTrx.getId();
    this.points = pointsTrx.getPoints();
    this.trxType = pointsTrx.getTrxType();
    this.description = pointsTrx.getDescription();
    this.trxDate = pointsTrx.getCreatedAt();
  }

  public PointsTrxDto toDto(PointsTrx pointsTrx) {
    return new PointsTrxDto(pointsTrx);
  }

}
