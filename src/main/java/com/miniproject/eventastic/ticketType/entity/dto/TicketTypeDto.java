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

  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer seatLimit;
  private Integer availableSeat;

  public TicketTypeDto(TicketType ticketType) {
    this.id = ticketType.getId();
    this.name = ticketType.getName();
    this.description = ticketType.getDescription();
    this.price = ticketType.getPrice();
    this.seatLimit = ticketType.getSeatLimit();
    this.availableSeat = ticketType.getAvailableSeat();
  }

  public TicketTypeDto toDto(TicketType ticketType) {
    return new TicketTypeDto(ticketType);
  }

  public TicketType toTicketTypeEntity(TicketTypeDto ticketTypeDto) {
    TicketType ticketType = new TicketType();
    ticketType.setId(ticketTypeDto.getId());
    ticketType.setName(ticketTypeDto.getName());
    ticketType.setDescription(ticketTypeDto.getDescription());
    ticketType.setPrice(ticketTypeDto.getPrice());
    ticketType.setSeatLimit(ticketTypeDto.getSeatLimit());
    ticketType.setAvailableSeat(ticketTypeDto.getAvailableSeat());
    return ticketType;
  }
}
