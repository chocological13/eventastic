package com.miniproject.eventastic.ticket.service.impl;

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
    ticket.setCode(UUID.randomUUID().toString().substring(0, 8));
    ticket.setIssuedAt(Instant.now());
    ticketRepository.save(ticket);
    return ticket;
  }

  @Override
  public Set<Ticket> findTicketsByUser(Users user) {
    return ticketRepository.findByUser(user);
  }
}
