package com.miniproject.eventastic.dashboard.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventStatisticsDto {
  private Long id;
  private String title;
  private Long uniqueAttendees;
  private Long uniqueRegistrants;
  private Boolean isFree;
  private BigDecimal grossRevenue;
  private BigDecimal netRevenue;
  private Long totalTicketsSold;
  private Double averageTicketPrice;
  private final String disclaimer = "A 2% service fee has been deducted from your account. The amount reflected in "
                                    + "the nett revenue is the final amount that you get!";
}
