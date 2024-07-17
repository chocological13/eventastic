package com.miniproject.eventastic.ticketType.entity.dto.create;

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
public class TicketTypeCreateRequestDto {

  @NotEmpty
  private String name;

  private String description;

  private BigDecimal price;

  @NotNull
  private Integer seatLimit;

  public TicketTypeCreateRequestDto(TicketType ticketType) {
    this.name = ticketType.getName();
    this.description = ticketType.getDescription();
    this.price = ticketType.getPrice();
    this.seatLimit = ticketType.getSeatLimit();
  }

  public static TicketTypeCreateRequestDto toTicketTypeRequestDto(TicketType ticketType) {
    return new TicketTypeCreateRequestDto(ticketType);
  }

  public static TicketType requestToTicketTypeEntity(TicketTypeCreateRequestDto ticketTypeCreateRequestDto) {
    TicketType ticketType = new TicketType();
    ticketType.setName(ticketTypeCreateRequestDto.getName());
    ticketType.setDescription(ticketTypeCreateRequestDto.getDescription());
    ticketType.setPrice(ticketTypeCreateRequestDto.getPrice());
    ticketType.setSeatLimit(ticketTypeCreateRequestDto.getSeatLimit());
    return ticketType;
  }
}
