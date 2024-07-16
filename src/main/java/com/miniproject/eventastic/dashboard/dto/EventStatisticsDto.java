package com.miniproject.eventastic.dashboard.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class EventStatisticsDto {
  private Long eventId;
  private String eventTitle;
  private Long totalAttendees;
  private BigDecimal totalRevenue;
  private Long ticketsSold;
  private Double averageTicketPrice;
  private Long registrations;

  public EventStatisticsDto(Long eventId, String eventTitle, Long totalAttendees, BigDecimal totalRevenue,
      Long ticketsSold, Double averageTicketPrice, Long registrations) {
    this.eventId = eventId;
    this.eventTitle = eventTitle;
    this.totalAttendees = totalAttendees;
    this.totalRevenue = totalRevenue;
    this.ticketsSold = ticketsSold;
    this.averageTicketPrice = averageTicketPrice;
    this.registrations = registrations;
  }
}
