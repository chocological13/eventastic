package com.miniproject.eventastic.ticket.service.impl;

import com.miniproject.eventastic.exceptions.trx.TicketNotFoundException;
import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.ticket.repository.TicketRepository;
import com.miniproject.eventastic.ticket.service.TicketService;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.users.entity.Users;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

  private final TicketRepository ticketRepository;


  @Override
  public void saveTicket(Ticket ticket) {
    ticketRepository.save(ticket);
  }

  @Override
  public Ticket generateTicket(Users user, TicketType ticketType) {
    Ticket ticket = new Ticket();
    ticket.setTicketType(ticketType);
    ticket.setUser(user);
    ticket.setEvent(ticketType.getEvent());
    ticket.setCode(generateTicketCode());
    ticket.setIssuedAt(Instant.now());
    ticketRepository.save(ticket);
    return ticket;
  }

  public String generateTicketCode() {
    String ticketCode;
    Ticket ticket;
    do {
      ticketCode = UUID.randomUUID().toString().substring(0, 8);
      ticket = ticketRepository.findByCode(ticketCode);
    } while (ticket != null);
      return ticketCode;
  }

  @Override
  public Set<Ticket> findTicketsByUser(Users user) {
    Set<Ticket> ticketSet = ticketRepository.findByUser(user);
    if (ticketSet == null) {
      throw new TicketNotFoundException("No ticket found");
    }
    return ticketSet;
  }
}
