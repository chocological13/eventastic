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
public class TicketTypeRequestDto {

  @NotEmpty
  private String name;

  private String description;

  @NotNull
  private BigDecimal price;

  @NotNull
  private Integer seatLimit;

  public TicketTypeRequestDto(TicketType ticketType) {
    this.name = ticketType.getName();
    this.description = ticketType.getDescription();
    this.price = ticketType.getPrice();
    this.seatLimit = ticketType.getSeatLimit();
  }

  public static TicketTypeRequestDto toTicketTypeRequestDto(TicketType ticketType) {
    return new TicketTypeRequestDto(ticketType);
  }

  public static TicketType requestToTicketTypeEntity(TicketTypeRequestDto ticketTypeRequestDto) {
    TicketType ticketType = new TicketType();
    ticketType.setName(ticketTypeRequestDto.getName());
    ticketType.setDescription(ticketTypeRequestDto.getDescription());
    ticketType.setPrice(ticketTypeRequestDto.getPrice());
    ticketType.setSeatLimit(ticketTypeRequestDto.getSeatLimit());
    return ticketType;
  }
}
