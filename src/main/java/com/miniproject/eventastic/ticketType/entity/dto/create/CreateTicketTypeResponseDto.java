package com.miniproject.eventastic.ticketType.entity.dto.create;

import com.miniproject.eventastic.ticketType.entity.TicketType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketTypeResponseDto {

  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer seatLimit;
  private Integer availableSeat;

  public CreateTicketTypeResponseDto(TicketType ticketType) {
    this.id = ticketType.getId();
    this.name = ticketType.getName();
    this.description = ticketType.getDescription();
    this.price = ticketType.getPrice();
    this.seatLimit = ticketType.getSeatLimit();
    this.availableSeat = ticketType.getAvailableSeat();
  }

  public CreateTicketTypeResponseDto toDto(TicketType ticketType) {
    return new CreateTicketTypeResponseDto(ticketType);
  }

  public TicketType toTicketTypeEntity(CreateTicketTypeResponseDto createTicketTypeResponseDto) {
    TicketType ticketType = new TicketType();
    ticketType.setId(createTicketTypeResponseDto.getId());
    ticketType.setName(createTicketTypeResponseDto.getName());
    ticketType.setDescription(createTicketTypeResponseDto.getDescription());
    ticketType.setPrice(createTicketTypeResponseDto.getPrice());
    ticketType.setSeatLimit(createTicketTypeResponseDto.getSeatLimit());
    ticketType.setAvailableSeat(createTicketTypeResponseDto.getAvailableSeat());
    return ticketType;
  }
}
