package com.miniproject.eventastic.trx.service.impl;

import com.miniproject.eventastic.exceptions.trx.TicketNotFoundException;
import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.ticket.service.TicketService;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.trx.entity.dto.TrxPurchaseRequestDto;
import com.miniproject.eventastic.trx.event.TicketPurchasedEvent;
import com.miniproject.eventastic.trx.service.TrxService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
@Transactional
public class TrxServiceImpl implements TrxService {

  private final UsersService usersService;
  private final TicketService ticketService;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public Trx purchaseTicket(TrxPurchaseRequestDto requestDto) {
    // * get logged-in user
    Users loggedUser = usersService.getCurrentUser();
    Trx trx = new Trx();
    eventPublisher.publishEvent(new TicketPurchasedEvent(this, loggedUser, trx, requestDto));

    return trx;
  }

  @Override
  public Set<Ticket> getUserTickets() {
    Users loggedUser = usersService.getCurrentUser();
    Set<Ticket> ticketSet = ticketService.findTicketsByUser(loggedUser);
    if (ticketSet.isEmpty()) {
      throw new TicketNotFoundException("You have not purchased anything as of late :(");
    } else {
      return ticketSet;
    }
  }
}
