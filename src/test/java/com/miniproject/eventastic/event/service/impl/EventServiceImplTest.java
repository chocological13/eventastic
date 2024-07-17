package com.miniproject.eventastic.event.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.repository.CategoryRepository;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.review.service.ReviewService;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.service.VoucherService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

@SpringBootTest
public class EventServiceImplTest {
  @Mock
  private EventRepository eventRepository;
  @Mock
  private TicketTypeService ticketTypeService;
  @Mock
  private UsersService usersService;
  @Mock
  private ImageService imageService;
  @Mock
  private CategoryRepository categoryRepository;
  @Mock
  private ReviewService reviewService;
  @Mock
  private AttendeeService attendeeService;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private VoucherService voucherService;

  @InjectMocks
  private EventServiceImpl eventService = new EventServiceImpl(eventRepository, ticketTypeService, usersService,
      imageService, categoryRepository, reviewService, attendeeService, eventPublisher, voucherService);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    eventService = new EventServiceImpl(eventRepository, ticketTypeService, usersService,
        imageService, categoryRepository, reviewService, attendeeService, eventPublisher, voucherService);
  }

  @Test
  void getEvents_Success() {
    // Arrange
    int page = 0;
    int size = 10;
    Pageable pageable = PageRequest.of(page, size);
    Event event1 = new Event();
    event1.setId(1L);
    event1.setTitle("Test Event 1");
    Event event2 = new Event();
    event2.setId(2L);
    event2.setTitle("Test Event 2");
    Page<Event> eventPage = new PageImpl<>(Arrays.asList(event1, event2));

    when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(eventPage);

    // Act
    Page<EventResponseDto> result = eventService.getEvents(page, size, null, null, null, null, null, null, null);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getTotalElements());
    assertEquals("Test Event 1", result.getContent().get(0).getTitle());
    assertEquals("Test Event 2", result.getContent().get(1).getTitle());
  }

  @Test
  void getEvents_NoContent_ThrowsException() {
    // Arrange
    int page = 0;
    int size = 10;
    Pageable pageable = PageRequest.of(page, size);
    Page<Event> emptyPage = new PageImpl<>(Arrays.asList());

    when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

    // Act & Assert
    assertThrows(EventNotFoundException.class, () ->
        eventService.getEvents(page, size, null, null, null, null, null, null, null)
    );
  }

  @Test
  void getEventById_Success() {
    // Arrange
    Long eventId = 1L;
    Event event = new Event();
    event.setId(eventId);
    event.setTitle("Test Event");

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

    // Act
    Event result = eventService.getEventById(eventId);

    // Assert
    assertNotNull(result);
    assertEquals(eventId, result.getId());
    assertEquals("Test Event", result.getTitle());
  }

  @Test
  void getEventById_NotFound_ThrowsException() {
    // Arrange
    Long eventId = 1L;
    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EventNotFoundException.class, () -> eventService.getEventById(eventId));
  }

  @Test
  void deleteEvent_Success() {
    // Arrange
    Long eventId = 1L;
    Event event = new Event();
    event.setId(eventId);
    Users organizer = new Users();
    event.setOrganizer(organizer);

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(usersService.getCurrentUser()).thenReturn(organizer);

    // Act
    eventService.deleteEvent(eventId);

    // Assert
    assertNotNull(event.getDeletedAt());
  }

  @Test
  void deleteEvent_NotOrganizer_ThrowsException() {
    // Arrange
    Long eventId = 1L;
    Event event = new Event();
    event.setId(eventId);
    Users organizer = new Users();
    event.setOrganizer(organizer);
    Users differentUser = new Users();

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(usersService.getCurrentUser()).thenReturn(differentUser);

    // Act & Assert
    assertThrows(AccessDeniedException.class, () -> eventService.deleteEvent(eventId));
  }

//  @Test
//  void isEventEnded_True() {
//    // Arrange
//    Event event = new Event();
//    event.setEventDate(LocalDate.now().minusDays(1));
//    event.setEndTime(LocalTime.NOON);
//
//    // Act
//    boolean result = eventService.isEventEnded(event);
//
//    // Assert
//    assertTrue(result);
//  }
//
//  @Test
//  void isEventEnded_False() {
//    // Arrange
//    Event event = new Event();
//    event.setEventDate(LocalDate.now().plusDays(1));
//    event.setEndTime(LocalTime.NOON);
//
//    // Act
//    boolean result = eventService.isEventEnded(event);
//
//    // Assert
//    assertFalse(result);
//  }

}
