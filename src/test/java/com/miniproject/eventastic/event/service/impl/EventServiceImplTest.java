//package com.miniproject.eventastic.event.service.impl;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.miniproject.eventastic.attendee.entity.Attendee;
//import com.miniproject.eventastic.attendee.entity.AttendeeId;
//import com.miniproject.eventastic.attendee.service.AttendeeService;
//import com.miniproject.eventastic.event.entity.Event;
//import com.miniproject.eventastic.event.repository.CategoryRepository;
//import com.miniproject.eventastic.event.repository.EventRepository;
//import com.miniproject.eventastic.event.service.EventService;
//import com.miniproject.eventastic.image.service.ImageService;
//import com.miniproject.eventastic.review.entity.Review;
//import com.miniproject.eventastic.review.entity.dto.ReviewSubmitRequestDto;
//import com.miniproject.eventastic.review.service.ReviewService;
//import com.miniproject.eventastic.ticketType.service.TicketTypeService;
//import com.miniproject.eventastic.users.entity.Users;
//import com.miniproject.eventastic.users.service.UsersService;
//import java.time.LocalDate;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//@SpringBootTest
//public class EventServiceImplTest {
//
//  // create a mock implementation of EventRepository
//  @Mock
//  private EventRepository eventRepository;
//  @Mock
//  private TicketTypeService ticketTypeService;
//  @Mock
//  private UsersService usersService;
//  @Mock
//  private ImageService imageService;
//  @Mock
//  private CategoryRepository categoryRepository;
//  @Mock
//  private ReviewService reviewService;
//  @Mock
//  private AttendeeService attendeeService;
//  @Mock
//  private Authentication authentication;
//  @Mock
//  private SecurityContext securityContext;
//
//
//  // tells eventService to use the mock repository
//  @InjectMocks
//  private EventService eventService = new EventServiceImpl(eventRepository, ticketTypeService, usersService,
//      imageService, categoryRepository, reviewService, attendeeService);
//
//  private Users eventOrganizer;
//  private Users userAttendee;
//  private Event event;
//
//  // before each test, run this. init mock objects
//  @BeforeEach
//  public void setUp() {
//    MockitoAnnotations.openMocks(this);
//    eventService = new EventServiceImpl(eventRepository, ticketTypeService, usersService,
//        imageService, categoryRepository, reviewService, attendeeService);
//
//    eventOrganizer = new Users();
//    eventOrganizer.setId(1L);
//    eventOrganizer.setUsername("Organizer");
//
//    userAttendee = new Users();
//    userAttendee.setId(2L);
//    userAttendee.setUsername("Attendee");
//
//    event = new Event();
//    event.setId(1L);
//    event.setTitle("Existing Event");
//    event.setEventDate(LocalDate.now());
//    event.setOrganizer(eventOrganizer);
//    eventService.saveEvent(event);
//    when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
//
//    SecurityContextHolder.setContext(securityContext);
//  }
//
//
//  @Test
//  public void testSubmitReview() {
//    Attendee attendee = new Attendee();
//    AttendeeId attendeeId = new AttendeeId();
//
//    attendeeId.setUserId(userAttendee.getId());
//    attendeeId.setEventId(event.getId());
//    attendee.setAttendedAt(event.getEventDate());
//
//    event.setOrganizer(eventOrganizer);
//    attendee.setEvent(event);
//    attendee.setUser(userAttendee);
//
//    ReviewSubmitRequestDto requestDto = new ReviewSubmitRequestDto();
//    requestDto.setReviewMsg("Great event!");
//    requestDto.setRating(5);
//
//    when(usersService.getCurrentUser()).thenReturn(userAttendee);
//    when(attendeeService.findAttendee(new AttendeeId(userAttendee.getId(), 1L))).thenReturn(Optional.of(attendee));
//
//    Review review = eventService.submitReview(event.getId(), requestDto);
//
//    assertNotNull(review);
//  }
//
////  // Region - Tests for isDuplicateEvent
////
////  @Test
////  public void testIsDuplicateEvent_identicalEventExists() {
////    CreateEventRequestDto requestDto = new CreateEventRequestDto();
////    requestDto.setTitle("Sample Event");
////    requestDto.setLocation("123 Main St");
////    requestDto.setEventDate(LocalDate.of(2023, 7, 1));
////    requestDto.setStartTime(LocalTime.of(10, 0));
////
////    Event existingEvent = new Event();
////    existingEvent.setTitle("Sample Event");
////    existingEvent.setLocation("123 Main St");
////    existingEvent.setEventDate(LocalDate.of(2023, 7, 1));
////    existingEvent.setStartTime(LocalTime.of(10, 0));
////
////    when(eventRepository.findByTitleAndLocationAndEventDateAndStartTime(
////        requestDto.getTitle(), requestDto.getLocation(), requestDto.getEventDate(), requestDto.getStartTime()
////    )).thenReturn(Optional.of(existingEvent));
////
////    Boolean isDuplicate = eventService.isDuplicateEvent(requestDto);
////
////    assertTrue(isDuplicate, "Identical event should be marked as duplicate");
////  }
////
////  @Test
////  public void testIsDuplicateEvent_noIdenticalEvent() {
////    CreateEventRequestDto requestDto = new CreateEventRequestDto();
////    requestDto.setTitle("Sample Event");
////    requestDto.setLocation("123 Main St");
////    requestDto.setEventDate(LocalDate.of(2023, 7, 1));
////    requestDto.setStartTime(LocalTime.of(10, 0));
////
////    when(eventRepository.findByTitleAndLocationAndEventDateAndStartTime(
////        requestDto.getTitle(), requestDto.getLocation(), requestDto.getEventDate(), requestDto.getStartTime()
////    )).thenReturn(Optional.empty());
////
////    Boolean isDuplicate = eventService.isDuplicateEvent(requestDto);
////
////    assertFalse(isDuplicate, "No identical event should not be marked as duplicate");
////  }
////
////  @Test
////  public void testIsDuplicateEvent_partialMatchEvent() {
////    CreateEventRequestDto requestDto = new CreateEventRequestDto();
////    requestDto.setTitle("Sample Event");
////    requestDto.setLocation("123 Main St");
////    requestDto.setEventDate(LocalDate.of(2023, 7, 1));
////    requestDto.setStartTime(LocalTime.of(10, 0));
////
////    Event differentEvent = new Event();
////    differentEvent.setTitle("Sample Event");
////    differentEvent.setLocation("123 Main St");
////    differentEvent.setEventDate(LocalDate.of(2023, 7, 2)); // Different date
////    differentEvent.setStartTime(LocalTime.of(11, 0)); // Different time
////
////    when(eventRepository.findByTitleAndLocationAndEventDateAndStartTime(
////        requestDto.getTitle(), requestDto.getLocation(), requestDto.getEventDate(), requestDto.getStartTime()
////    )).thenReturn(Optional.empty());
////
////    Boolean isDuplicate = eventService.isDuplicateEvent(requestDto);
////
////    assertFalse(isDuplicate, "Partial match should not be marked as duplicate");
////  }
////
////  // End
////
////  // Region - TDD for update event
//////  @Test
//////  public void testUpdateEvent_success() {
//////    UpdateEventRequestDto updateEventRequestDto = new UpdateEventRequestDto();
//////    updateEventRequestDto.setTitle("Updated Event");
//////    updateEventRequestDto.setLocation("456 Another St");
//////    updateEventRequestDto.setCategory(Category.CONFERENCE);
//////    updateEventRequestDto.setVenue("New Venue");
//////    updateEventRequestDto.setEventDate(LocalDate.of(2024, 7, 15));
//////    updateEventRequestDto.setStartTime(LocalTime.of(10, 0));
//////    updateEventRequestDto.setEndTime(LocalTime.of(12, 0));
//////
//////    Event existingEvent = new Event();
//////    existingEvent.setId(1L);
//////    existingEvent.setTitle("Original Event");
//////    existingEvent.setLocation("123 Main St");
//////    existingEvent.setCategory(Category.WORKSHOP);
//////    existingEvent.setVenue("Old Venue");
//////    existingEvent.setEventDate(LocalDate.of(2024, 7, 14));
//////    existingEvent.setStartTime(LocalTime.of(9, 0));
//////    existingEvent.setEndTime(LocalTime.of(11, 0));
//////    existingEvent.setOrganizer(eventOrganizer);
//////    existingEvent.setSeatLimit(100);
//////    existingEvent.setAvailableSeat(100);
//////
//////    when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
//////    when(usersService.getCurrentUser()).thenReturn(eventOrganizer);
//////    when(eventRepository.save(existingEvent)).thenReturn(existingEvent);
//////
//////    EventResponseDto updatedEvent = eventService.updateEvent(1L, updateEventRequestDto);
//////
//////    assertEquals("Updated Event", updatedEvent.getTitle());
//////    assertEquals("456 Another St", updatedEvent.getLocation());
//////    assertEquals(Category.CONFERENCE, updatedEvent.getCategory());
//////    assertEquals("New Venue", updatedEvent.getVenue());
//////    assertEquals(LocalDate.of(2024, 7, 15), updatedEvent.getEventDate());
//////    assertEquals(LocalTime.of(10, 0), updatedEvent.getStartTime());
//////    assertEquals(LocalTime.of(12, 0), updatedEvent.getEndTime());
//////  }
////
////  @Test
////  public void testUpdateEvent_success() {
////
////    // Image in Event
////    Image image = new Image();
////    image.setId(1L);
////    // Image to update
////    Image image2 = new Image();
////    image2.setId(2L);
////
////    // Prepare update request DTO
////    UpdateEventRequestDto updateEventRequestDto = new UpdateEventRequestDto();
////    updateEventRequestDto.setTitle("Updated Event");
////    updateEventRequestDto.setLocation("456 Another St");
////    updateEventRequestDto.setCategory(Event.Category.CONFERENCE);
////    updateEventRequestDto.setVenue("New Venue");
////    updateEventRequestDto.setImageId(2L);
////    updateEventRequestDto.setEventDate(LocalDate.of(2024, 7, 15));
////    updateEventRequestDto.setStartTime(LocalTime.of(10, 0));
////    updateEventRequestDto.setEndTime(LocalTime.of(12, 0));
////
////    // Prepare existing event in repository
////    Event existingEvent = new Event();
////    existingEvent.setId(1L);
////    existingEvent.setTitle("Original Event");
////    existingEvent.setLocation("123 Main St");
////    existingEvent.setCategory(Event.Category.WORKSHOP);
////    existingEvent.setVenue("Old Venue");
////    existingEvent.setImage(image);
////    existingEvent.setEventDate(LocalDate.of(2024, 7, 14));
////    existingEvent.setStartTime(LocalTime.of(9, 0));
////    existingEvent.setEndTime(LocalTime.of(11, 0));
////    existingEvent.setOrganizer(eventOrganizer); // Assuming eventOrganizer is mocked
////    existingEvent.setSeatLimit(100);
////    existingEvent.setAvailableSeat(100);
////
////    // Mock repository methods
////    when(eventRepository.save(existingEvent)).thenReturn(existingEvent);
////    when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
////    when(usersService.getCurrentUser()).thenReturn(eventOrganizer); // Mock the logged-in user
////    when(imageRepository.findById(2L)).thenReturn(Optional.of(image2));
////
////    // Call the method
////    EventResponseDto updatedEvent = eventService.updateEvent(1L, updateEventRequestDto);
////
////    // Assertions
////    assertEquals("Updated Event", updatedEvent.getTitle());
////    assertEquals("456 Another St", updatedEvent.getLocation());
////    assertEquals(Event.Category.CONFERENCE, updatedEvent.getCategory());
////    assertEquals("New Venue", updatedEvent.getVenue());
////    assertEquals(image2, existingEvent.getImage());
////    assertEquals(LocalDate.of(2024, 7, 15), updatedEvent.getEventDate());
////    assertEquals(LocalTime.of(10, 0), updatedEvent.getStartTime());
////    assertEquals(LocalTime.of(12, 0), updatedEvent.getEndTime());
////
////    // Verify save was called with updatedEvent
////    verify(eventRepository).save(any(Event.class));
////  }
////
////  @Test
////  public void testUpdateEvent_notOriginalOrganizer() {
////    UpdateEventRequestDto updateEventRequestDto = new UpdateEventRequestDto();
////    updateEventRequestDto.setTitle("Updated Event");
////    updateEventRequestDto.setLocation("456 Another St");
////    updateEventRequestDto.setCategory(Event.Category.CONFERENCE);
////    updateEventRequestDto.setVenue("New Venue");
////    updateEventRequestDto.setEventDate(LocalDate.of(2024, 7, 15));
////    updateEventRequestDto.setStartTime(LocalTime.of(10, 0));
////    updateEventRequestDto.setEndTime(LocalTime.of(12, 0));
////
////    Event existingEvent = new Event();
////    existingEvent.setId(1L);
////    existingEvent.setTitle("Original Event");
////    existingEvent.setLocation("123 Main St");
////    existingEvent.setCategory(Event.Category.WORKSHOP);
////    existingEvent.setVenue("Old Venue");
////    existingEvent.setEventDate(LocalDate.of(2024, 7, 14));
////    existingEvent.setStartTime(LocalTime.of(9, 0));
////    existingEvent.setEndTime(LocalTime.of(11, 0));
////    existingEvent.setOrganizer(eventOrganizer);
////
////    Users anotherUser = new Users();
////    anotherUser.setId(2L);
////    anotherUser.setUsername("anotherUser");
////
////    when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
////    when(usersService.getCurrentUser()).thenReturn(anotherUser);
////
////    assertThrows(AccessDeniedException.class, () -> eventService.updateEvent(1L, updateEventRequestDto));
////  }
////
////  @Test
////  public void testUpdateEvent_eventNotFound() {
////    UpdateEventRequestDto updateEventRequestDto = new UpdateEventRequestDto();
////    updateEventRequestDto.setTitle("Updated Event");
////    updateEventRequestDto.setLocation("456 Another St");
////    updateEventRequestDto.setCategory(Event.Category.CONFERENCE);
////    updateEventRequestDto.setVenue("New Venue");
////    updateEventRequestDto.setEventDate(LocalDate.of(2024, 7, 15));
////    updateEventRequestDto.setStartTime(LocalTime.of(10, 0));
////    updateEventRequestDto.setEndTime(LocalTime.of(12, 0));
////
////    when(eventRepository.findById(1L)).thenReturn(Optional.empty());
////
////    assertThrows(EventNotFoundException.class, () -> eventService.updateEvent(1L, updateEventRequestDto));
////  }
////
////  @Test
////  public void testDeleteEvent_success() {
////    Event eventToDelete = new Event();
////    eventToDelete.setId(1L);
////    eventToDelete.setOrganizer(eventOrganizer);
////
////    when(eventRepository.findById(1L)).thenReturn(Optional.of(eventToDelete));
////    when(usersService.getCurrentUser()).thenReturn(eventOrganizer);
////
////    eventService.deleteEvent(1L);
////
////    assertEquals(Instant.now(), eventToDelete.getDeletedAt());
////  }
////
////  @Test
////  public void testDeleteEvent_notOrganizer() {
////    Users otherUser = new Users();
////    Event eventToDelete = new Event();
////    eventToDelete.setId(1L);
////    eventToDelete.setOrganizer(otherUser);
////
////    when(eventRepository.findById(1L)).thenReturn(Optional.of(eventToDelete));
////    when(usersService.getCurrentUser()).thenReturn(eventOrganizer);
////
////    assertThrows(AccessDeniedException.class, () -> eventService.deleteEvent(1L));
////  }
////
////  @Test
////  public void testDeleteEvent_eventNotFound() {
////    when(eventRepository.findById(1L)).thenReturn(Optional.empty());
////
////    assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(1L));
////  }
//}
