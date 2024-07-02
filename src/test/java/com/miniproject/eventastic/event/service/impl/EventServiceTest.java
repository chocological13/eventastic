package com.miniproject.eventastic.event.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.ticketType.repository.TicketTypeRepository;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EventServiceTest {

  // create a mock implementation of EventRepository
  @Mock
  private EventRepository eventRepository;

  @Mock
  private TicketTypeRepository ticketTypeRepository;

  @Mock
  private UsersService usersService;

  // tells eventService to use the mock repository
  @InjectMocks
  private EventService eventService = new EventServiceImpl(eventRepository, ticketTypeRepository, usersService);

  // before each test, run this. init mock objects
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    eventService = new EventServiceImpl(eventRepository, ticketTypeRepository, usersService);
  }

  // Region - Tests for isDuplicateEvent

  @Test
  public void testIsDuplicateEvent_identicalEventExists() {
    CreateEventRequestDto requestDto = new CreateEventRequestDto();
    requestDto.setTitle("Sample Event");
    requestDto.setLocation("123 Main St");
    requestDto.setEventDate(LocalDate.of(2023, 7, 1));
    requestDto.setStartTime(LocalTime.of(10, 0));

    Event existingEvent = new Event();
    existingEvent.setTitle("Sample Event");
    existingEvent.setLocation("123 Main St");
    existingEvent.setEventDate(LocalDate.of(2023, 7, 1));
    existingEvent.setStartTime(LocalTime.of(10, 0));

    when(eventRepository.findByTitleAndLocationAndEventDateAndStartTime(
        requestDto.getTitle(), requestDto.getLocation(), requestDto.getEventDate(), requestDto.getStartTime()
    )).thenReturn(Optional.of(existingEvent));

    Boolean isDuplicate = eventService.isDuplicateEvent(requestDto);

    assertTrue(isDuplicate, "Identical event should be marked as duplicate");
  }

  @Test
  public void testIsDuplicateEvent_noIdenticalEvent() {
    CreateEventRequestDto requestDto = new CreateEventRequestDto();
    requestDto.setTitle("Sample Event");
    requestDto.setLocation("123 Main St");
    requestDto.setEventDate(LocalDate.of(2023, 7, 1));
    requestDto.setStartTime(LocalTime.of(10, 0));

    when(eventRepository.findByTitleAndLocationAndEventDateAndStartTime(
        requestDto.getTitle(), requestDto.getLocation(), requestDto.getEventDate(), requestDto.getStartTime()
    )).thenReturn(Optional.empty());

    Boolean isDuplicate = eventService.isDuplicateEvent(requestDto);

    assertFalse(isDuplicate, "No identical event should not be marked as duplicate");
  }

  @Test
  public void testIsDuplicateEvent_partialMatchEvent() {
    CreateEventRequestDto requestDto = new CreateEventRequestDto();
    requestDto.setTitle("Sample Event");
    requestDto.setLocation("123 Main St");
    requestDto.setEventDate(LocalDate.of(2023, 7, 1));
    requestDto.setStartTime(LocalTime.of(10, 0));

    Event differentEvent = new Event();
    differentEvent.setTitle("Sample Event");
    differentEvent.setLocation("123 Main St");
    differentEvent.setEventDate(LocalDate.of(2023, 7, 2)); // Different date
    differentEvent.setStartTime(LocalTime.of(11, 0)); // Different time

    when(eventRepository.findByTitleAndLocationAndEventDateAndStartTime(
        requestDto.getTitle(), requestDto.getLocation(), requestDto.getEventDate(), requestDto.getStartTime()
    )).thenReturn(Optional.empty());

    Boolean isDuplicate = eventService.isDuplicateEvent(requestDto);

    assertFalse(isDuplicate, "Partial match should not be marked as duplicate");
  }

  // End

}
