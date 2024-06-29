package com.miniproject.eventastic.event.service.impl;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.entity.dto.TicketTypeDto;
import com.miniproject.eventastic.ticketType.repository.TicketTypeRepository;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final TicketTypeRepository ticketTypeRepository;
  private final UsersService usersService;

  @Override
  public EventResponseDto createEvent(CreateEventRequestDto requestDto) {
    CreateEventRequestDto e = new CreateEventRequestDto();
    Event createdEvent = e.dtoToEvent(requestDto);

    // extract user
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String organizerName = auth.getName();
    Users organizer = usersService.getByUsername(organizerName);
    createdEvent.setOrganizer(organizer);

    // save event here so we can set it to the ticket types
    eventRepository.save(createdEvent);

    // get ticket type
    Set<TicketTypeDto> ticketTypeDtos = requestDto.getTicketTypes();
    Set<TicketType> ticketTypes = new HashSet<>();
    // map ticket type
    for (TicketTypeDto ticketTypeDto : ticketTypeDtos) {
      TicketType ticketType = ticketTypeDto.toTicketType(ticketTypeDto);
      ticketType.setEvent(createdEvent); // Associate with the created Event

      ticketTypes.add(ticketType);
      ticketTypeRepository.save(ticketType);
    }
    log.info("ticketTypeDtos: {}", ticketTypeDtos);
    log.info("ticketTypes: {}", ticketTypes);
    
    // update seat limit
    int totalSeatLimit = ticketTypes.stream().mapToInt(TicketType::getSeatLimit).sum();
    createdEvent.setSeatLimit(totalSeatLimit);
    createdEvent.setAvailableSeat(totalSeatLimit);

    // update save
    eventRepository.save(createdEvent);

    EventResponseDto responseDto = new EventResponseDto();
    return responseDto.toDto(createdEvent, ticketTypeDtos);
  }
}
