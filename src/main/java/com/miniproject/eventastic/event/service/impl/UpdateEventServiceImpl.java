package com.miniproject.eventastic.event.service.impl;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.event.service.UpdateEventService;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.trx.TicketNotFoundException;
import com.miniproject.eventastic.exceptions.trx.TicketTypeNotFoundException;
import com.miniproject.eventastic.image.entity.ImageEvent;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.entity.dto.update.TicketTypeUpdateRequestDto;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateEventServiceImpl implements UpdateEventService {
  private final EventRepository eventRepository;
  private final UsersService usersService;
  private final ImageService imageService;
  private final TicketTypeService ticketTypeService;


  @Override
  public EventResponseDto updateEvent(Long eventId, UpdateEventRequestDto requestDto) throws RuntimeException {
    // get existing event
    Optional<Event> optionalEvent = eventRepository.findById(eventId);
    if (optionalEvent.isEmpty()) {
      throw new EventNotFoundException("Event not found, please enter a valid ID");
    }
    Event existingEvent = optionalEvent.get();

    // verify organizer
    verifyOrganizer(existingEvent);

    // dto to entity
    updateEventDetails(existingEvent, requestDto);

    // set image, if any
    setImage(existingEvent, requestDto);

    // update ticket types, if any
    setTicketTypes(existingEvent, requestDto);

    // set map, if any
    if (requestDto.getMap() != null) {
      existingEvent.setMap(requestDto.getMap());
    }


    // save event
    eventRepository.save(existingEvent);
    return new EventResponseDto(existingEvent);
  }

  // * get logged-in user and verify identity as organizer that created the event
  public void verifyOrganizer(Event event) throws AccessDeniedException {
    Users loggedUser = usersService.getCurrentUser();
    if (loggedUser != event.getOrganizer()) {
      throw new AccessDeniedException("You do not have permission to update this event");
    }
  }

  public void updateEventDetails(Event updatedEvent, UpdateEventRequestDto requestDto) {
    UpdateEventRequestDto dto = new UpdateEventRequestDto();
    dto.dtoToEvent(updatedEvent, requestDto);
  }

  public void setImage(Event updatedEvent, UpdateEventRequestDto requestDto) {
    if (requestDto.getImageId() != null) {
      ImageEvent eventImage = imageService.getEventImageById(requestDto.getImageId());
      if (eventImage != null) {
        updatedEvent.setEventImage(eventImage);
      }
    }
  }

  public void setTicketTypes(Event updatedEvent, UpdateEventRequestDto requestDto) {
    if (requestDto.getTicketTypeUpdates() != null) {
      Set<TicketType> existingTicketTypes = updatedEvent.getTicketTypes();
      Set<TicketTypeUpdateRequestDto> requestTicketTypeDtos = requestDto.getTicketTypeUpdates();
      for (TicketTypeUpdateRequestDto requestTicketTypeDto : requestTicketTypeDtos) {
        TicketType ticketType;
        if (requestTicketTypeDto.getTicketTypeId() != null) {
          ticketType = existingTicketTypes.stream()
              .filter(tt -> tt.getId().equals(requestTicketTypeDto.getTicketTypeId()))
              .findFirst()
              .orElseThrow(() -> new TicketNotFoundException(
                  "Ticket type with ID " + requestTicketTypeDto.getTicketTypeId() + " not found"));

          updateExistingTicketType(existingTicketTypes, ticketType, requestTicketTypeDto);
        } else {
          throw new TicketTypeNotFoundException("Please specify ticket type ID");
        }
      }
      updatedEvent.setTicketTypes(existingTicketTypes);
    }
  }

  public void updateExistingTicketType(Set<TicketType> existingTicketTypes, TicketType ticketType,
      TicketTypeUpdateRequestDto ticketTypeUpdateRequestDto) {
    if (ticketTypeUpdateRequestDto.getDescription() != null) {
      ticketType.setDescription(ticketTypeUpdateRequestDto.getDescription());
    }

    if (ticketTypeUpdateRequestDto.getPrice() != null) {
      ticketType.setPrice(ticketTypeUpdateRequestDto.getPrice());
    }

    if (ticketTypeUpdateRequestDto.getSeatLimit() != null) {
      ticketType.setSeatLimit(ticketTypeUpdateRequestDto.getSeatLimit());
      ticketType.setSeatAvailability(ticketTypeUpdateRequestDto.getSeatLimit());
    }
    ticketTypeService.saveTicketType(ticketType);
    existingTicketTypes.add(ticketType);
  }

}
