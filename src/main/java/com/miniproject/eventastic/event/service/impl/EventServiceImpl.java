package com.miniproject.eventastic.event.service.impl;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.entity.AttendeeId;
import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.dashboard.dto.EventStatisticsDto;
import com.miniproject.eventastic.dashboard.dto.EventSummaryDto;
import com.miniproject.eventastic.dashboard.dto.MonthlyRevenueDto;
import com.miniproject.eventastic.dashboard.dto.OrganizerDashboardSummaryDto;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.event.repository.CategoryRepository;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.ObjectNotFoundException;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.event.ReviewNotFoundException;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
  public Page<EventResponseDto> getEvents(int page, int size, String title, String category, String location,
      String organizer, Boolean isFree, String order, String direction) throws EventNotFoundException {

    // sort direction, by default ascending
    Direction sortDirection = Direction.fromString(order == null ? "asc" : direction);
    // sort order, by default eventDate
    Sort sortOrder = Sort.by(sortDirection, order == null ? "eventDate" : order);
    // init pageable
    Pageable pageable = PageRequest.of(page, size, sortOrder);

    // init specification for filtering
    Specification<Event> specification = Specification.where(null);
    // props
    if (title != null && !title.isEmpty()) {
      specification = specification.and(EventSpecifications.hasTitle(title));
    }
    if (category != null && !category.isEmpty()) {
      specification = specification.and(EventSpecifications.hasCategory(category));
    }
    if (location != null && !location.isEmpty()) {
      specification = specification.and(EventSpecifications.hasLocation(location));
    }
    if (organizer != null && !organizer.isEmpty()) {
      specification = specification.and(EventSpecifications.hasOrganizer(organizer));
    }
    if (isFree != null) {
      specification = specification.and(EventSpecifications.isFree(isFree));
    }

    Page<Event> eventsPage = eventRepository.findAll(specification, pageable);
    if (!eventsPage.hasContent()) {
      throw new EventNotFoundException("Event by that specification does not exist.");
    }
    return eventsPage.map(EventResponseDto::new);
  }

  @Override
  public Page<EventResponseDto> getByKeyword(int page, int size, String keyword) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());

    Specification<Event> specification = EventSpecifications.hasKeyword(keyword);

    Page<Event> eventsPage = eventRepository.findAll(specification, pageable);
    if (!eventsPage.hasContent()) {
      throw new EventNotFoundException("No upcoming events found.");
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
  public Event getEventById(Long eventId) throws EventNotFoundException {
    Optional<Event> event = eventRepository.findById(eventId);
    if (event.isPresent()) {
      return event.get();
    } else {
      throw new EventNotFoundException("Event not found, please enter a valid ID");
    }
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
    return categoryRepository.findById(eventId)
        .orElseThrow(() -> new EventNotFoundException("Event by ID: " + eventId + " does not exist."));
  }

  @Override
  public Review submitReview(Long eventId, ReviewSubmitRequestDto requestDto) throws RuntimeException {
    Users reviewer = usersService.getCurrentUser();
    Event event = getEventById(eventId);

    // Check if an attendee
    Attendee attendee = attendeeService.findAttendee(new AttendeeId(reviewer.getId(), eventId))
        .orElseThrow(() -> new AttendeeNotFoundException("You are not an attendee of this event!"));

    // Check if event is done or not
    if (!isEventEnded(event)) {
      throw new IllegalArgumentException("You may only submit review if event has been commenced!");
    }
    // Check if the user has already submitted a review for this event
    if (reviewService.hasSubmittedReview(reviewer, event)) {
      throw new DuplicateCredentialsException("You have already submitted a review for this event.");
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

  private boolean isEventEnded(Event event) {
    LocalDate eventDate = event.getEventDate();
    LocalTime endTime = event.getEndTime();

    LocalDateTime eventEndDateTime = LocalDateTime.of(eventDate, endTime);
    LocalDateTime now = LocalDateTime.now();

    return now.isAfter(eventEndDateTime);
  }

  @Override
  public Page<ReviewSubmitResponseDto> getEventReviews(Long eventId, int page, int size)
      throws ReviewNotFoundException {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return reviewService.getReviewsByEventId(eventId, pageable);
  }

  @Override
  public ImageEvent uploadEventImage(ImageUploadRequestDto requestDto)
      throws IllegalArgumentException, AccessDeniedException {
    Users organizer = usersService.getCurrentUser();
    if (!organizer.getIsOrganizer()) {
      throw new AccessDeniedException("You do not have permission to upload an image for an event!");
    }
    return imageService.uploadEventImage(requestDto, organizer);
  }

  @Override
  public Page<EventStatisticsDto> getEventStatistics(Users organizer, Pageable pageable) throws RuntimeException {
    Page<EventStatisticsDto> statisticsDtoPage = eventRepository.getEventStatisticsDto(organizer, pageable);
    if (!statisticsDtoPage.hasContent()) {
      throw new EventNotFoundException("You have not organized any events!");
    }
    return statisticsDtoPage;
  }

  @Override
  public Page<Event> getEventBetweenDates(Users organizer, LocalDate startDate, LocalDate endDate, Pageable pageable)
  throws RuntimeException {
    Page<Event> eventPage = eventRepository.findByOrganizerAndEventDateBetween(organizer, startDate,
        endDate, pageable);
    if (eventPage.isEmpty()) {
      throw new EventNotFoundException("Events between date " + startDate + " and " + endDate + " not found");
    }
    return eventPage;
  }

  @Override
  public List<MonthlyRevenueDto> getMonthlyRevenue(Users organizer, Integer year) {
    List<MonthlyRevenueDto> monthlyRevenueList =  eventRepository.getMonthlyRevenueByOrganizer(organizer, year);
    if (monthlyRevenueList.isEmpty()) {
      throw new ObjectNotFoundException("You have no revenue reports for this year yet!");
    }
    return monthlyRevenueList;
  }

  @Override
  public OrganizerDashboardSummaryDto getOrganizerDashboardSummary(Users organizer) {
    OrganizerDashboardSummaryDto organizerDashboardSummaryDto = eventRepository.getDashboardSummary(organizer);
    if (organizerDashboardSummaryDto == null) {
      throw new ObjectNotFoundException("You have no summary to display!");
    }
    return organizerDashboardSummaryDto;
  }

  @Override
  public List<EventSummaryDto> getEventSummary(Users organizer) {
    List<EventSummaryDto> eventSummaryDtos = eventRepository.getEventSummary(organizer);
    if (eventSummaryDtos.isEmpty()) {
      throw new ObjectNotFoundException("You have no summary to display!");
    }
    return eventSummaryDtos;
  }

  // * get logged-in user and verify identity as organizer that created the event
  public void verifyOrganizer(Event event) throws AccessDeniedException {
    Users loggedUser = usersService.getCurrentUser();
    if (loggedUser != event.getOrganizer()) {
      throw new AccessDeniedException("You do not have permission to update this event");
    }
  }

}
