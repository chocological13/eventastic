package com.miniproject.eventastic.dashboard.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrganizerDashboardSummaryDto {

  private Long totalEvents;
  private Long upcomingEvents;
  private BigDecimal nettRevenue;
  private Long totalAttendees;
  private Double averageNettRevenuePerEvent;
  private String disclaimer;

  public OrganizerDashboardSummaryDto(Long totalEvents, Long upcomingEvents, BigDecimal nettRevenue,
      Long totalAttendees, Double averageNettRevenuePerEvent) {
    this.totalEvents = totalEvents;
    this.upcomingEvents = upcomingEvents;
    this.nettRevenue = nettRevenue;
    this.totalAttendees = totalAttendees;
    this.averageNettRevenuePerEvent = averageNettRevenuePerEvent;
    this.disclaimer = "Service fee of 2% has been deducted from your total amount. The amount reflected here is the end"
                      + " result";
  }

}
