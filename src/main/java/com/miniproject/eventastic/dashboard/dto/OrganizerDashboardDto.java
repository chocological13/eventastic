package com.miniproject.eventastic.dashboard.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrganizerDashboardDto {

  private Long totalEvents;
  private Integer upcomingEvents;
  private BigDecimal netRevenue;
  private Integer totalAttendees;
  private BigDecimal averageNetRevenuePerEvent;

}
