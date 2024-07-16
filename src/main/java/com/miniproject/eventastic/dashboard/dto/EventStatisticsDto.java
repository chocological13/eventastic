package com.miniproject.eventastic.dashboard.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventStatisticsDto {
  private Long id;
  private String title;
  private Long uniqueAttendees;
  private Long uniqueRegistrants;
  private BigDecimal grossRevenue;
  private Long totalTicketsSold;
  private Double averageTicketPrice;

  public EventStatisticsDto(Long id, String title, Long uniqueAttendees, Long uniqueRegistrants,
      BigDecimal grossRevenue, Long totalTicketsSold, Double averageTicketPrice) {
    this.id = id;
    this.title = title;
    this.uniqueAttendees = uniqueAttendees;
    this.uniqueRegistrants = uniqueRegistrants;
    this.grossRevenue = grossRevenue;
    this.totalTicketsSold = totalTicketsSold;
    this.averageTicketPrice = averageTicketPrice;
  }
}
