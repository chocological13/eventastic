package com.miniproject.eventastic.event.event.EventUpdated.listener;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;
import com.miniproject.eventastic.event.event.EventUpdated.EventUpdatedEvent;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.trx.TicketNotFoundException;
import com.miniproject.eventastic.exceptions.trx.TicketTypeNotFoundException;
import com.miniproject.eventastic.image.entity.ImageUserAvatar;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.entity.dto.update.TicketTypeUpdateRequestDto;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventUpdatedListener {

  private final EventService eventService;
  private final ImageService imageService;
  private final TicketTypeService ticketTypeService;

  @EventListener
  @Transactional
  public void handleEventUpdatedEvent(EventUpdatedEvent event) {
    Event updatedEvent = event.getEvent();
    UpdateEventRequestDto requestDto = event.getRequestDto();

    // dto to entity
    updateEventDetails(updatedEvent, requestDto);

    // set image, if any
    setImage(updatedEvent, requestDto);

    // update ticket types, if any
    setTicketTypes(updatedEvent, requestDto);
  }

  // Region - utilities
  private void updateEventDetails(Event updatedEvent, UpdateEventRequestDto requestDto) {
    UpdateEventRequestDto dto = new UpdateEventRequestDto();
    dto.dtoToEvent(updatedEvent, requestDto);
  }

  private void setImage(Event updatedEvent, UpdateEventRequestDto requestDto) {
    if (requestDto.getImageId() != null) {
      ImageUserAvatar imageUserAvatar = imageService.getImageById(requestDto.getImageId());
      if (imageUserAvatar != null) {
        updatedEvent.setImageUserAvatar(imageUserAvatar);
      }
    }
  }

  private void setTicketTypes(Event updatedEvent, UpdateEventRequestDto requestDto) {
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

  private void updateExistingTicketType(Set<TicketType> existingTicketTypes, TicketType ticketType,
      TicketTypeUpdateRequestDto ticketTypeUpdateRequestDto) {
    if (ticketTypeUpdateRequestDto.getDescription() != null) {
      ticketType.setDescription(ticketTypeUpdateRequestDto.getDescription());
    }

    if (ticketTypeUpdateRequestDto.getPrice() != null) {
      ticketType.setPrice(ticketTypeUpdateRequestDto.getPrice());
    }

    if (ticketTypeUpdateRequestDto.getSeatLimit() != null) {
      ticketType.setSeatLimit(ticketTypeUpdateRequestDto.getSeatLimit());
      ticketType.setAvailableSeat(ticketTypeUpdateRequestDto.getSeatLimit());
    }
    ticketTypeService.saveTicketType(ticketType);
    existingTicketTypes.add(ticketType);
  }

}
