package com.miniproject.eventastic.dashboard.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueDto {
  private YearMonth yearMonth;
  private BigDecimal grossRevenue;
  private BigDecimal netRevenue;
  private Long eventCount;
  private Long totalTicketsSold;
  private String disclaimer;

  public MonthlyRevenueDto(int year, int month, BigDecimal grossRevenue, BigDecimal netRevenue,
      Long eventCount, Long totalTicketsSold) {
    this.yearMonth = YearMonth.of(year, month);
    this.grossRevenue = grossRevenue;
    this.netRevenue = netRevenue;
    this.eventCount = eventCount;
    this.totalTicketsSold = totalTicketsSold;
    this.disclaimer = "A service fee of 2% is automatically deducted from your total revenue.";
  }
}