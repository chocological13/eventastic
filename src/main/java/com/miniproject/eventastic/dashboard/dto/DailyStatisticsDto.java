package com.miniproject.eventastic.dashboard.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;

@Data
public class DailyStatisticsDto {

  private Instant date;
  private Long newRegistrations;
  private BigDecimal dailyGrossRevenue;
  private BigDecimal dailyNetRevenue;
  private Long ticketsSold;
  private String disclaimer;

  public DailyStatisticsDto(Instant date, Long newRegistrations, BigDecimal dailyGrossRevenue,
      BigDecimal dailyNetRevenue, Long ticketsSold) {
    this.date = date;
    this.newRegistrations = newRegistrations;
    this.dailyGrossRevenue = dailyGrossRevenue;
    this.dailyNetRevenue = dailyNetRevenue;
    this.ticketsSold = ticketsSold;
    this.disclaimer = "A service fee of 2% is automatically deducted from your balance";
  }
}
