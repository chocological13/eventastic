package com.miniproject.eventastic.ticketType.entity.dto.update;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TicketTypeUpdateRequestDto {

  @NotEmpty
  private Long ticketTypeId;
  private String ticketTypeName;
  private String description;
  @NotNull
  private BigDecimal price;
  @NotNull
  private Integer seatLimit;

}
