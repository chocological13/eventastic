package com.miniproject.eventastic.event.service.impl;

import com.miniproject.eventastic.event.entity.Category;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;
import com.miniproject.eventastic.event.repository.CategoryRepository;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.event.CategoryNotFoundException;
import com.miniproject.eventastic.exceptions.event.DuplicateEventException;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.entity.dto.create.CreateTicketTypeRequestDto;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final TicketTypeService ticketTypeService;
  private final UsersService usersService;
  private final ImageService imageService;
  private final CategoryRepository categoryRepository;

  @Override
  public EventResponseDto createEvent(CreateEventRequestDto requestDto) {
    // check if there's a duplicate
    if (isDuplicateEvent(requestDto)) {
      throw new DuplicateEventException("Event already exists. Please create another one.");
    }

    // extract user
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String organizerName = auth.getName();
    Optional<Users> organizerOptional = Optional.ofNullable(usersService.getByUsername(organizerName));

    // save event here, so we can set it to the ticket types
    Event createdEvent = requestDto.dtoToEvent(requestDto);
    organizerOptional.ifPresent(createdEvent::setOrganizer);
    eventRepository.save(createdEvent);

    // check for category
    if (requestDto.getCategoryId() != null) {
      Category category = getCategoryById(requestDto.getCategoryId());
      if (category != null) {
        createdEvent.setCategory(category);
      } else {
        throw new CategoryNotFoundException("Category not found, please enter another ID");
      }
    }

    // check for image
    if (requestDto.getImageId() != null) {
      Image image = imageService.getImageById(requestDto.getImageId());
      if (image != null) {
        createdEvent.setImage(image);
      } else {
        throw new ImageNotFoundException("Image does not exist! Please enter a new ID.");
      }
    }

    // init ticket type
    Set<CreateTicketTypeRequestDto> createTicketTypeRequestDtos = requestDto.getCreateTicketTypeRequestDtos();
    Set<TicketType> ticketTypes = new HashSet<>();

    if (!createdEvent.getIsFree()) {
      // map ticket type
      for (CreateTicketTypeRequestDto createTicketTypeRequestDto : createTicketTypeRequestDtos) {
        TicketType ticketType = CreateTicketTypeRequestDto.requestToTicketTypeEntity(createTicketTypeRequestDto);
        ticketType.setEvent(createdEvent); // Associate with the created Event
        ticketType.setAvailableSeat(createTicketTypeRequestDto.getSeatLimit());

        ticketTypes.add(ticketType);
        ticketTypeService.saveTicketType(ticketType);
      }
    } else {

      // default ticket type if isFree is true
      TicketType freeTicketType = new TicketType();
      CreateTicketTypeRequestDto firstInSet = createTicketTypeRequestDtos.iterator().next();

      freeTicketType.setName("Free Admission");
      freeTicketType.setDescription("Free entry ticket");
      freeTicketType.setPrice(BigDecimal.ZERO);
      freeTicketType.setSeatLimit(firstInSet.getSeatLimit());
      freeTicketType.setAvailableSeat(firstInSet.getSeatLimit());
      freeTicketType.setEvent(createdEvent);

      ticketTypes.add(freeTicketType);
      ticketTypeService.saveTicketType(freeTicketType);
    }

    // update seat limit
    int totalSeatLimit = ticketTypes.stream().mapToInt(TicketType::getSeatLimit).sum();
    createdEvent.setSeatLimit(totalSeatLimit);
    createdEvent.setAvailableSeat(totalSeatLimit);

    // update save
    // * Clearing and Adding to the Collection:
    // This ensures that Hibernate correctly manages the state of the collection and avoids orphaned entities.
    createdEvent.getTicketTypes().clear();
    createdEvent.getTicketTypes().addAll(ticketTypes);
    eventRepository.save(createdEvent);

    return new EventResponseDto(createdEvent);
  }

  @Override
  public Page<EventResponseDto> getEvents(int page, int size, String title, String category, String location,
      String order, String direction) {

    // sort direction, by default ascending
    Sort.Direction sortDirection = Sort.Direction.fromString(order == null ? "asc" : direction);
    // sort order, by default eventDate
    Sort sortOrder = Sort.by(sortDirection, order == null ? "eventDate" : order);
    // init pageable
    Pageable pageable = PageRequest.of(page, size, sortOrder);

    // init specification for filtering
    Specification<Event> specification = Specification.where(null);
    // props
    if (title != null) {
      specification = specification.and(EventSpecifications.hasTitle(title));
    }
    if (category != null) {
      specification = specification.and(EventSpecifications.hasCategory(category));
    }
    if (location != null) {
      specification = specification.and(EventSpecifications.hasLocation(location));
    }

    Page<Event> eventsPage = eventRepository.findAll(specification, pageable);
    return eventsPage.map(EventResponseDto::new);
  }

  @Override
  public Page<EventResponseDto> getUpcomingEvents(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());

    Specification<Event> specification = EventSpecifications.isUpcoming();

    Page<Event> eventsPage = eventRepository.findAll(specification, pageable);
    return eventsPage.map(EventResponseDto::new);
  }

  @Override
  public Event getEventById(Long eventId) {
    return eventRepository.findById(eventId).orElse(null);
  }


  @Override
  public Boolean isDuplicateEvent(CreateEventRequestDto checkDuplicate) {
    String title = checkDuplicate.getTitle();
    String location = checkDuplicate.getLocation();
    LocalDate eventDate = checkDuplicate.getEventDate();
    LocalTime startTime = checkDuplicate.getStartTime();
    Optional<Event> checkEvent = eventRepository.findByTitleAndLocationAndEventDateAndStartTime(title, location, eventDate, startTime);
    return checkEvent.isPresent();
  }

  @Override
  public EventResponseDto updateEvent(Long eventId, UpdateEventRequestDto requestDto) {
    // get logged in user
    Users loggedUser = usersService.getCurrentUser();

    // verify event organizer with logged-in user
    Optional<Event> optionalEvent = eventRepository.findById(eventId);
    if (optionalEvent.isEmpty()) {
      throw new EventNotFoundException("Event not found");
    }
    if (loggedUser != optionalEvent.get().getOrganizer()) {
      throw new AccessDeniedException("You do not have permission to update this event");
    }

    // update event
    UpdateEventRequestDto dto = new UpdateEventRequestDto();
    Event updatedEvent = dto.dtoToEvent(optionalEvent.get(), requestDto);

    // check for image
    if (requestDto.getImageId() != null) {
      Image image = imageService.getImageById(requestDto.getImageId());
      if (image != null) {
        updatedEvent.setImage(image);
      }
    }

    // save event
    eventRepository.save(updatedEvent);
    return new EventResponseDto(updatedEvent);
  }

  @Override
  public void deleteEvent(Long eventId) {
    // get logged in user
    Users loggedUser = usersService.getCurrentUser();

    // verify event organizer with logged-in user
    Optional<Event> optionalEvent = eventRepository.findById(eventId);
    if (optionalEvent.isEmpty()) {
      throw new EventNotFoundException("Event not found");
    }
    if (loggedUser != optionalEvent.get().getOrganizer()) {
      throw new AccessDeniedException("You do not have permission to update this event");
    }

    Event eventToDelete = optionalEvent.get();
    eventToDelete.setDeletedAt(Instant.now());
  }

  @Override
  public Category getCategoryById(Long eventId) {
    return categoryRepository.findById(eventId).orElse(null);
  }

}
