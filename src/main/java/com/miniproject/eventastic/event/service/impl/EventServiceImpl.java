package com.miniproject.eventastic.event.service.impl;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.entity.AttendeeId;
import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;
import com.miniproject.eventastic.event.event.EventCreatedEvent;
import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.event.repository.CategoryRepository;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.event.CategoryNotFoundException;
import com.miniproject.eventastic.exceptions.event.DuplicateEventException;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.exceptions.trx.TicketTypeNotFoundException;
import com.miniproject.eventastic.exceptions.user.AttendeeNotFoundException;
import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitRequestDto;
import com.miniproject.eventastic.review.service.ReviewService;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.entity.dto.create.TicketTypeCreateRequestDto;
import com.miniproject.eventastic.ticketType.entity.dto.update.TicketTypeUpdateRequestDto;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import jakarta.transaction.Transactional;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
  private final ReviewService reviewService;
  private final AttendeeService attendeeService;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void saveEvent(Event event) {
    eventRepository.save(event);
  }

  @Override
  @Transactional
  public EventResponseDto createEvent(CreateEventRequestDto requestDto) {
    // check if there's a duplicate
    if (isDuplicateEvent(requestDto)) {
      throw new DuplicateEventException("Event already exists. Please create another one.");
    }

    // extract user
    Users organizer = usersService.getCurrentUser();

    // save event here, so we can set it to the ticket types
    Event createdEvent = requestDto.dtoToEvent(requestDto);
    createdEvent.setOrganizer(organizer);
    eventRepository.save(createdEvent);

    // event listener
    eventPublisher.publishEvent(new EventCreatedEvent(this, createdEvent, requestDto));

    return new EventResponseDto(createdEvent);
  }

  @Override
  public Page<EventResponseDto> getEvents(int page, int size, String title, String category, String location,
      String order, String direction) {

    // sort direction, by default ascending
    Direction sortDirection = Direction.fromString(order == null ? "asc" : direction);
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
    Optional<Event> event = eventRepository.findById(eventId);
    if (event.isPresent()) {
      return event.get();
    } else {
      throw new EventNotFoundException("Event not found, please enter a valid ID");
    }
  }

  @Override
  public EventResponseDto updateEvent(Long eventId, UpdateEventRequestDto requestDto) {
    // get existing event
    Event existingEvent = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventNotFoundException("Event not found, please enter a valid ID"));

    // update event
    updateEventDetails(existingEvent, requestDto);

    // check for image
    setImage(existingEvent, requestDto);

    // update ticket type
    if (requestDto.getTicketTypeUpdates() != null) {
      Set<TicketType> existingTicketTypes = existingEvent.getTicketTypes();
      for (TicketTypeUpdateRequestDto dtoTicketType : requestDto.getTicketTypeUpdates()) {
        TicketType ticketType;
        if (dtoTicketType.getTicketTypeId() != null) {
          ticketType = existingTicketTypes.stream()
              .filter(tt -> tt.getId().equals(dtoTicketType.getTicketTypeId()))
              .findFirst()
              .orElse(new TicketType());
          updateExistingTicketType(ticketType, dtoTicketType);
          existingTicketTypes.add(ticketType);
        } else {
          throw new TicketTypeNotFoundException("Ticket Type not found!");
        }
      }
      existingEvent.setTicketTypes(existingTicketTypes);
    }
    // save event
    eventRepository.save(existingEvent);
    return new EventResponseDto(existingEvent);
  }

  @Override
  public void deleteEvent(Long eventId) {
    Event eventToDelete = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventNotFoundException("Event not found, please enter a valid ID"));
    // verify if the logged-in user is the organizer for this event
    if (verifyOrganizer(eventToDelete)) {
      eventToDelete.setDeletedAt(Instant.now());
    }
  }

  @Override
  public Category getCategoryById(Long eventId) {
    return categoryRepository.findById(eventId).orElse(null);
  }

  @Override
  public Review submitReview(Long eventId, ReviewSubmitRequestDto requestDto) {
    Users reviewer = usersService.getCurrentUser();
    Event event = getEventById(eventId);
    Attendee attendee = attendeeService.findAttendee(new AttendeeId(reviewer.getId(), eventId)).orElse(null);

    if (attendee == null) {
      throw new AttendeeNotFoundException("Attendee not found");
    }

    Review review = new Review();
    review.setReviewer(reviewer);
    review.setOrganizer(event.getOrganizer());
    review.setEvent(event);
    review.setReview(requestDto.getReviewMsg());
    review.setRating(requestDto.getRating());
    reviewService.saveReview(review);
    return review;
  }

  @Override
  public Set<Review> getEventReviews(Long eventId) {
    return reviewService.getReviewsByEventId(eventId);
  }

  // Region - utilities for update event
  // * get logged-in user and verify identity as organizer that created the event
  private boolean verifyOrganizer(Event event) {
    Users loggedUser = usersService.getCurrentUser();
    if (loggedUser != event.getOrganizer()) {
      throw new AccessDeniedException("You do not have permission to update this event");
    }
    return true;
  }

  private void updateEventDetails(Event event, UpdateEventRequestDto requestDto) {
    if (verifyOrganizer(event)) {
      UpdateEventRequestDto dto = new UpdateEventRequestDto();
      dto.dtoToEvent(event, requestDto);
    }
  }

  private void setImage(Event event, UpdateEventRequestDto requestDto) {
    if (requestDto.getImageId() != null) {
      Image image = imageService.getImageById(requestDto.getImageId());
      if (image != null) {
        event.setImage(image);
      }
    }
  }

  private void updateExistingTicketType (TicketType ticketType, TicketTypeUpdateRequestDto ticketTypeUpdateRequestDto) {
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
  }

  // Region - utilities
  private Boolean isDuplicateEvent(CreateEventRequestDto checkDuplicate) {
    String title = checkDuplicate.getTitle();
    String location = checkDuplicate.getLocation();
    LocalDate eventDate = checkDuplicate.getEventDate();
    LocalTime startTime = checkDuplicate.getStartTime();
    Optional<Event> checkEvent = eventRepository.findByTitleAndLocationAndEventDateAndStartTime(title, location,
        eventDate, startTime);
    return checkEvent.isPresent();
  }

}
