package com.miniproject.eventastic.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventSummaryDto {

  private Long id;
  private String title;
  private LocalDate eventDate;
  private Long totalAttendees;
  private Boolean isFree;
  private BigDecimal totalNettRevenue;
  private Long ticketsSold;
  private Long seatAvailability;
  private final String disclaimer = "A service fee of 2% has been deducted, the amount reflected is the final amount "
                                  + "you receive";

}
