package com.miniproject.eventastic.ticket.entity.dto;

import com.miniproject.eventastic.ticket.entity.Ticket;
import java.time.Instant;
import lombok.Data;

@Data
public class TrxIssuedTicketDto {

  private Long id;
  private Instant issuedAt;
  private String attendee;
  private String eventTitle;
  private String ticketType;
  private String ticketCode;

  public TrxIssuedTicketDto(Ticket ticket) {
    this.id = ticket.getId();
    this.issuedAt = ticket.getIssuedAt();
    this.attendee = ticket.getUser().getUsername();
    this.eventTitle = ticket.getEvent().getTitle();
    this.ticketType = ticket.getTicketType().getName();
    this.ticketCode = ticket.getCode();
  }

  public TrxIssuedTicketDto toDto(Ticket ticket) {
    return new TrxIssuedTicketDto(ticket);
  }

}
