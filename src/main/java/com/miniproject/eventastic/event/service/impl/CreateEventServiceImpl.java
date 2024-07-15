package com.miniproject.eventastic.event.service.impl;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.event.repository.CategoryRepository;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.event.service.CreateEventService;
import com.miniproject.eventastic.exceptions.event.CategoryNotFoundException;
import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.exceptions.user.DuplicateCredentialsException;
import com.miniproject.eventastic.image.entity.ImageEvent;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.entity.dto.create.TicketTypeCreateRequestDto;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateEventVoucherRequestDto;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateEventServiceImpl implements CreateEventService {

  private final EventRepository eventRepository;
  private final UsersService usersService;
  private final CategoryRepository categoryRepository;
  private final TicketTypeService ticketTypeService;
  private final ImageService imageService;
  private final VoucherService voucherService;

  @Override
  @Transactional
  public EventResponseDto createEvent(CreateEventRequestDto requestDto) throws RuntimeException {
    // * 1. check if there's a duplicate
    if (isDuplicateEvent(requestDto)) {
      throw new DuplicateCredentialsException("Event already exists. Please create another one.");
    }

    // * 2. extract user
    Users organizer = usersService.getCurrentUser();
    if (!organizer.getIsOrganizer()) {
      throw new AccessDeniedException("You are not an organizer!");
    }

    // * 3 save event here, so we can set it to the ticket types
    Event newEvent = requestDto.dtoToEvent(requestDto);
    newEvent.setOrganizer(organizer);
    eventRepository.save(newEvent);

    // * 4 set category
    setCategory(newEvent, requestDto);

    // * 5 set image
    setImage(newEvent, requestDto);

    // * 6 init ticket type and set it
    Set<TicketTypeCreateRequestDto> ticketTypeCreateRequestDtos = requestDto.getTicketTypeRequestDtos();
    Set<TicketType> ticketTypes = getTicketType(newEvent, ticketTypeCreateRequestDtos);

    // * 7 update seat limit
    setSeatLimit(newEvent, ticketTypes);

    // * 8. create voucher, if any
    Set<Voucher> eventVouchers = new LinkedHashSet<>();
    if (requestDto.getVoucherRequestDto() != null) {
      CreateEventVoucherRequestDto voucherDto = requestDto.getVoucherRequestDto();
      Voucher eventVoucher = voucherService.createEventVoucher(organizer, newEvent, voucherDto);
      eventVouchers.add(eventVoucher);
      newEvent.setVouchers(eventVouchers);
    }

    // * set map
    if (requestDto.getMap() != null) {
      newEvent.setMap(requestDto.getMap());
    }

    eventRepository.save(newEvent);

    return new EventResponseDto(newEvent);
  }

  public Boolean isDuplicateEvent(CreateEventRequestDto checkDuplicate) {
    String title = checkDuplicate.getTitle();
    String location = checkDuplicate.getLocation();
    LocalDate eventDate = checkDuplicate.getEventDate();
    LocalTime startTime = checkDuplicate.getStartTime();
    Optional<Event> checkEvent = eventRepository.findByTitleAndLocationAndEventDateAndStartTime(title, location,
        eventDate, startTime);
    return checkEvent.isPresent();
  }

  public void setCategory(Event createdEvent, CreateEventRequestDto requestDto) throws CategoryNotFoundException {
    if (requestDto.getCategoryId() != null) {
      Category category = categoryRepository.findById(requestDto.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("Category not found, please enter another ID"));
      createdEvent.setCategory(category);
    }
  }

  public void setImage(Event createdEvent, CreateEventRequestDto requestDto) throws ImageNotFoundException {
    if (requestDto.getImageId() != null) {
      ImageEvent imageEvent = imageService.getEventImageById(requestDto.getImageId());
      createdEvent.setEventImage(imageEvent);
    }
  }

  public Set<TicketType> getTicketType(Event createdEvent,
      Set<TicketTypeCreateRequestDto> ticketTypeCreateRequestDtos) {
    Set<TicketType> ticketTypes = new LinkedHashSet<>();
    if (!createdEvent.getIsFree()) {
      for (TicketTypeCreateRequestDto ticketTypeCreateRequestDto : ticketTypeCreateRequestDtos) {
        TicketType ticketType = TicketTypeCreateRequestDto.requestToTicketTypeEntity(ticketTypeCreateRequestDto);
        ticketType.setEvent(createdEvent);
        ticketType.setSeatAvailability(ticketTypeCreateRequestDto.getSeatLimit());

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
      freeTicketType.setSeatAvailability(firstInSet.getSeatLimit());

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
    createdEvent.setSeatAvailability(totalSeats);
  }

}
