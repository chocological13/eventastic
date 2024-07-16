package com.miniproject.eventastic.dashboard.dto;

import com.miniproject.eventastic.trx.entity.Trx;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;

@Data
public class TrxDashboardDto {
  private Long id;
  private Long userId;
  private String username;
  private String eventTitle;
  private Integer qty;
  private BigDecimal totalAmount;
  private Instant trxDate;
  private Boolean isPaid;
  private String ticketType;

  public TrxDashboardDto(Trx trx) {
    this.id = trx.getId();
    this.userId = trx.getUser().getId();
    this.username = trx.getUser().getUsername();
    this.eventTitle = trx.getEvent().getTitle();
    this.qty = trx.getQty();
    this.totalAmount = trx.getTotalAmount();
    this.trxDate = trx.getTrxDate();
    this.isPaid = trx.getIsPaid();
    this.ticketType = trx.getTicketType().getName();
  }

  public TrxDashboardDto toDto(Trx trx) {
    return new TrxDashboardDto(trx);
  }
}
