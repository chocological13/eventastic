package com.miniproject.eventastic.event.service.impl;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.entity.AttendeeId;
import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;
import com.miniproject.eventastic.event.event.EventCreated.EventCreatedEvent;
import com.miniproject.eventastic.event.event.EventUpdated.EventUpdatedEvent;
import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.event.repository.CategoryRepository;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.event.CategoryNotFoundException;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.event.ReviewNotFoundException;
import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.exceptions.user.AttendeeNotFoundException;
import com.miniproject.eventastic.exceptions.user.DuplicateCredentialsException;
import com.miniproject.eventastic.image.entity.ImageEvent;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitRequestDto;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitResponseDto;
import com.miniproject.eventastic.review.service.ReviewService;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateEventVoucherRequestDto;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
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
  private final VoucherService voucherService;

  @Override
  public void saveEvent(Event event) {
    eventRepository.save(event);
  }

  @Override
  @Transactional
  public EventResponseDto createEvent(CreateEventRequestDto requestDto) throws DuplicateCredentialsException,
      AccessDeniedException, ImageNotFoundException, CategoryNotFoundException, EventNotFoundException {
    // check if there's a duplicate
    if (isDuplicateEvent(requestDto)) {
      throw new DuplicateCredentialsException("Event already exists. Please create another one.");
    }

    // extract user
    Users organizer = usersService.getCurrentUser();
    if (!organizer.getIsOrganizer()) {
      throw new AccessDeniedException("You are not an organizer!");
    }

    // save event here, so we can set it to the ticket types
    Event createdEvent = requestDto.dtoToEvent(requestDto);
    createdEvent.setOrganizer(organizer);
    eventRepository.save(createdEvent);

    // event listener
    eventPublisher.publishEvent(new EventCreatedEvent(this, createdEvent, requestDto));
    eventRepository.save(createdEvent);

    return new EventResponseDto(createdEvent);
  }

  @Override
  public Page<EventResponseDto> getEvents(int page, int size, String title, String category, String location,
      String organizer, String order, String direction) throws EventNotFoundException {

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
    if (organizer != null) {
      specification = specification.and(EventSpecifications.hasOrganizer(organizer));
    }

    Page<Event> eventsPage = eventRepository.findAll(specification, pageable);
    if (!eventsPage.hasContent()) {
      throw new EventNotFoundException("Event by that specification does not exist.");
    }
    return eventsPage.map(EventResponseDto::new);
  }

  @Override
  public Page<EventResponseDto> getUpcomingEvents(int page, int size) throws EventNotFoundException {
    Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());

    Specification<Event> specification = EventSpecifications.isUpcoming();

    Page<Event> eventsPage = eventRepository.findAll(specification, pageable);
    if (!eventsPage.hasContent()) {
      throw new EventNotFoundException("No upcoming events found.");
    }
    return eventsPage.map(EventResponseDto::new);
  }

  @Override
  public Page<EventResponseDto> getEventsByOrganizer(Long organizerId, int page, int size) throws EventNotFoundException {
    Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
    Specification<Event> specification = EventSpecifications.byOrganizerId(organizerId);
    Page<Event> eventsPage = eventRepository.findAll(specification, pageable);
    if (!eventsPage.hasContent()) {
      throw new EventNotFoundException("Events by organizer does not exist.");
    }
    return eventsPage.map(EventResponseDto::new);
  }

  @Override
  public Event getEventById(Long eventId) throws EventNotFoundException {
    Optional<Event> event = eventRepository.findById(eventId);
    if (event.isPresent()) {
      return event.get();
    } else {
      throw new EventNotFoundException("Event not found, please enter a valid ID");
    }
  }

  @Override
  public EventResponseDto updateEvent(Long eventId, UpdateEventRequestDto requestDto) throws EventNotFoundException,
      AccessDeniedException {
    // get existing event
    Event existingEvent = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventNotFoundException("Event not found, please enter a valid ID"));

    // verify organizer
    verifyOrganizer(existingEvent);
    eventPublisher.publishEvent(new EventUpdatedEvent(this, existingEvent, requestDto));

    // save event
    eventRepository.save(existingEvent);
    return new EventResponseDto(existingEvent);
  }

  @Override
  public void deleteEvent(Long eventId) throws EventNotFoundException, AccessDeniedException {
    Event eventToDelete = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventNotFoundException("Event not found, please enter a valid ID"));
    // verify if the logged-in user is the organizer for this event
    verifyOrganizer(eventToDelete);
    eventToDelete.setDeletedAt(Instant.now());

  }

  @Override
  public Voucher createEventVoucher(Long eventId, CreateEventVoucherRequestDto requestDto) throws RuntimeException {
    Users organizer = usersService.getCurrentUser();
    Event event = getEventById(eventId);
    return voucherService.createEventVoucher(organizer, event, requestDto);
  }

  @Override
  public Category getCategoryById(Long eventId) {
    return categoryRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event by ID: " + eventId + " does not exist."));
  }

  @Override
  public Review submitReview(Long eventId, ReviewSubmitRequestDto requestDto) throws EventNotFoundException,
      AccessDeniedException, AttendeeNotFoundException {
    Users reviewer = usersService.getCurrentUser();
    Event event = getEventById(eventId);
    Attendee attendee = attendeeService.findAttendee(new AttendeeId(reviewer.getId(), eventId)).orElse(null);

    if (attendee == null) {
      throw new AttendeeNotFoundException("You are not an attendee of this event!");
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

  // TODO : give pagination to this
  @Override
  public Page<ReviewSubmitResponseDto> getEventReviews(Long eventId, int page, int size) throws ReviewNotFoundException {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return reviewService.getReviewsByEventId(eventId, pageable);
  }

  @Override
  public ImageEvent uploadEventImage(ImageUploadRequestDto requestDto) throws IllegalArgumentException, AccessDeniedException {
    Users organizer = usersService.getCurrentUser();
    if (!organizer.getIsOrganizer()) {
      throw new AccessDeniedException("You do not have permission to upload an image for an event!");
    }
    return imageService.uploadEventImage(requestDto, organizer);
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

  // * get logged-in user and verify identity as organizer that created the event
  private void verifyOrganizer(Event event) throws AccessDeniedException {
    Users loggedUser = usersService.getCurrentUser();
    if (loggedUser != event.getOrganizer()) {
      throw new AccessDeniedException("You do not have permission to update this event");
    }
  }

}
