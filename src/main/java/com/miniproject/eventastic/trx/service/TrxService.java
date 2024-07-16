package com.miniproject.eventastic.trx.service;

import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.trx.entity.dto.TrxPurchaseRequestDto;
import com.miniproject.eventastic.users.entity.Users;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrxService {

  Trx purchaseTicket(TrxPurchaseRequestDto requestDto);

  // this will be called in /users
  Set<Ticket> getUserTickets();

  Page<Trx> getTrxsByOrganizer(Users organizer, Pageable pageable);
}
