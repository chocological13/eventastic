package com.miniproject.eventastic.dashboard.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventStatisticsDto {
  private Long id;
  private String title;
  private Long uniqueAttendees;
  private Long uniqueRegistrants;
  private BigDecimal totalRevenue;
  private Long totalTicketsSold;
  private Double averageTicketPrice;

  public EventStatisticsDto(Long id, String title, Long uniqueAttendees, Long uniqueRegistrants,
      BigDecimal totalRevenue, Long totalTicketsSold, Double averageTicketPrice) {
    this.id = id;
    this.title = title;
    this.uniqueAttendees = uniqueAttendees;
    this.uniqueRegistrants = uniqueRegistrants;
    this.totalRevenue = totalRevenue;
    this.totalTicketsSold = totalTicketsSold;
    this.averageTicketPrice = averageTicketPrice;
  }
}
