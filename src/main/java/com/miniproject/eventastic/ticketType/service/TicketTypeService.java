package com.miniproject.eventastic.ticketType.service;

import com.miniproject.eventastic.ticketType.entity.TicketType;

public interface TicketTypeService {

  void saveTicketType(TicketType ticketType);

  TicketType getTicketTypeById(Long id);
}
