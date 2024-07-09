package com.miniproject.eventastic.ticketType.entity.dto.create;

import com.miniproject.eventastic.ticketType.entity.TicketType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeCreateResponseDto {

  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer seatLimit;
  private Integer availableSeat;

  public TicketTypeCreateResponseDto(TicketType ticketType) {
    this.id = ticketType.getId();
    this.name = ticketType.getName();
    this.description = ticketType.getDescription();
    this.price = ticketType.getPrice();
    this.seatLimit = ticketType.getSeatLimit();
    this.availableSeat = ticketType.getAvailableSeat();
  }

  public TicketTypeCreateResponseDto toDto(TicketType ticketType) {
    return new TicketTypeCreateResponseDto(ticketType);
  }

  public TicketType toTicketTypeEntity(TicketTypeCreateResponseDto ticketTypeCreateResponseDto) {
    TicketType ticketType = new TicketType();
    ticketType.setId(ticketTypeCreateResponseDto.getId());
    ticketType.setName(ticketTypeCreateResponseDto.getName());
    ticketType.setDescription(ticketTypeCreateResponseDto.getDescription());
    ticketType.setPrice(ticketTypeCreateResponseDto.getPrice());
    ticketType.setSeatLimit(ticketTypeCreateResponseDto.getSeatLimit());
    ticketType.setAvailableSeat(ticketTypeCreateResponseDto.getAvailableSeat());
    return ticketType;
  }
}
