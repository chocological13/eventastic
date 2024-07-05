package com.miniproject.eventastic.ticketType.entity.dto.trx;

import com.miniproject.eventastic.ticketType.entity.TicketType;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TrxTicketTypeResponseDto {

  private String ticketType;
  private BigDecimal unitPrice;

  public TrxTicketTypeResponseDto(TicketType ticketType) {
    this.ticketType = ticketType.getName();
    this.unitPrice = ticketType.getPrice();
  }

  public TrxTicketTypeResponseDto toDto(TicketType ticketType) {
    return new TrxTicketTypeResponseDto(ticketType);
  }

}
