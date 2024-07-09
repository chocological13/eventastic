package com.miniproject.eventastic.event.event.listener;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.event.EventCreatedEvent;
import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.event.repository.CategoryRepository;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.exceptions.event.CategoryNotFoundException;
import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.entity.dto.create.TicketTypeCreateRequestDto;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import jakarta.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventCreatedEventListener {

  private final EventRepository eventRepository;
  private final CategoryRepository categoryRepository;
  private final ImageService imageService;
  private final TicketTypeService ticketTypeService;

  @EventListener
  @Transactional
  public void handleEventCreatedEvent(EventCreatedEvent event) {
    Event createdEvent = event.getEvent();
    CreateEventRequestDto requestDto = event.getRequestDto();

    // set category
    setCategory(createdEvent, requestDto);

    // set image
    setImage(createdEvent, requestDto);

    // init ticket type and set it
    Set<TicketTypeCreateRequestDto> ticketTypeCreateRequestDtos = requestDto.getTicketTypeCreateRequestDtos();
    Set<TicketType> ticketTypes = getTicketType(createdEvent, ticketTypeCreateRequestDtos);

    // update seat limit
    setSeatLimit(createdEvent, ticketTypes);
  }

  // Region - utilities
  private void setCategory(Event createdEvent, CreateEventRequestDto requestDto) {
    if (requestDto.getCategoryId() != null) {
      Category category = categoryRepository.findById(requestDto.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("Category not found, please enter another ID"));
      createdEvent.setCategory(category);
    }
  }

  private void setImage(Event createdEvent, CreateEventRequestDto requestDto) {
    if (requestDto.getImageId() != null) {
      Image image = imageService.getImageById(requestDto.getImageId());
      createdEvent.setImage(image);
    }
  }

  private Set<TicketType> getTicketType(Event createdEvent,
      Set<TicketTypeCreateRequestDto> ticketTypeCreateRequestDtos) {
    Set<TicketType> ticketTypes = new LinkedHashSet<>();
    if (!createdEvent.getIsFree()) {
      for (TicketTypeCreateRequestDto ticketTypeCreateRequestDto : ticketTypeCreateRequestDtos) {
        TicketType ticketType = TicketTypeCreateRequestDto.requestToTicketTypeEntity(ticketTypeCreateRequestDto);
        ticketType.setEvent(createdEvent);
        ticketType.setAvailableSeat(ticketTypeCreateRequestDto.getSeatLimit());

        // add in the set
        ticketTypes.add(ticketType);
        // save in repo
        ticketTypeService.saveTicketType(ticketType);
      }
    } else {
      // set default ticket type if free
      TicketType freeTicketType = new TicketType();
      // because it comes from a set, get the first in set
      TicketTypeCreateRequestDto firstInSet = ticketTypeCreateRequestDtos.iterator().next();

      freeTicketType.setEvent(createdEvent);
      freeTicketType.setName("Free Admission");
      freeTicketType.setDescription("Free entry ticket");
      freeTicketType.setSeatLimit(firstInSet.getSeatLimit());
      freeTicketType.setAvailableSeat(firstInSet.getSeatLimit());

      ticketTypes.add(freeTicketType);
      ticketTypeService.saveTicketType(freeTicketType);
    }
    createdEvent.getTicketTypes().clear();
    createdEvent.getTicketTypes().addAll(ticketTypes);
    return ticketTypes;
  }

  public void setSeatLimit(Event createdEvent, Set<TicketType> ticketTypes) {
    int totalSeats = ticketTypes.stream().mapToInt(TicketType::getSeatLimit).sum();
    createdEvent.setSeatLimit(totalSeats);
    createdEvent.setAvailableSeat(totalSeats);
  }

}
