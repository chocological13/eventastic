package com.miniproject.eventastic.trx.service;

import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.trx.entity.dto.TrxPurchaseRequestDto;
import java.util.Set;

public interface TrxService {

  Trx purchaseTicket(TrxPurchaseRequestDto requestDto);

  // this will be called in /users
  Set<Ticket> getUserTickets();
}
