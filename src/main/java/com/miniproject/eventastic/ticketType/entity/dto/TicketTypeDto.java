package com.miniproject.eventastic.ticketType.entity.dto;

import com.miniproject.eventastic.ticketType.entity.TicketType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeDto {

//  @NotNull
//  private Event event;

  @NotEmpty
  private String name;

  private String description;

  @NotNull
  private BigDecimal price;

  @NotNull
  private Integer seatLimit;

  public TicketType toTicketType(TicketTypeDto ticketTypeDto) {
    TicketType ticketType = new TicketType();
    ticketType.setName(ticketTypeDto.getName());
    ticketType.setDescription(ticketTypeDto.getDescription());
    ticketType.setPrice(ticketTypeDto.getPrice());
    ticketType.setSeatLimit(ticketTypeDto.getSeatLimit());
    return ticketType;
  }
}
