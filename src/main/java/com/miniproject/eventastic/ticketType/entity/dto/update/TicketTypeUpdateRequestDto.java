package com.miniproject.eventastic.ticketType.entity.dto.update;

import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TicketTypeUpdateRequestDto {

  @NotEmpty
  private Long ticketTypeId;
  private String ticketTypeName;
  private String description;
  private BigDecimal price;
  private Integer seatLimit;

}
