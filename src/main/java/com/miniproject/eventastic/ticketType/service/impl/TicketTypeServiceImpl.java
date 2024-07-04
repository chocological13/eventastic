package com.miniproject.eventastic.ticketType.service.impl;

import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.repository.TicketTypeRepository;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

  private final TicketTypeRepository ticketTypeRepository;

  @Override
  public void saveTicketType(TicketType ticketType) {
    ticketTypeRepository.save(ticketType);
  }
}
