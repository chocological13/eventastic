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
public class CreateTicketTypeRequestDto {

  @NotEmpty
  private String name;

  private String description;

  @NotNull
  private BigDecimal price;

  @NotNull
  private Integer seatLimit;

  public CreateTicketTypeRequestDto(TicketType ticketType) {
    this.name = ticketType.getName();
    this.description = ticketType.getDescription();
    this.price = ticketType.getPrice();
    this.seatLimit = ticketType.getSeatLimit();
  }

  public static CreateTicketTypeRequestDto toTicketTypeRequestDto(TicketType ticketType) {
    return new CreateTicketTypeRequestDto(ticketType);
  }

  public static TicketType requestToTicketTypeEntity(CreateTicketTypeRequestDto createTicketTypeRequestDto) {
    TicketType ticketType = new TicketType();
    ticketType.setName(createTicketTypeRequestDto.getName());
    ticketType.setDescription(createTicketTypeRequestDto.getDescription());
    ticketType.setPrice(createTicketTypeRequestDto.getPrice());
    ticketType.setSeatLimit(createTicketTypeRequestDto.getSeatLimit());
    return ticketType;
  }
}
