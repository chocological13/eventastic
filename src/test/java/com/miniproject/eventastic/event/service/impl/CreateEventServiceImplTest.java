package com.miniproject.eventastic.event.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.event.repository.CategoryRepository;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.exceptions.event.CategoryNotFoundException;
import com.miniproject.eventastic.exceptions.event.EventDateInvalidException;
import com.miniproject.eventastic.exceptions.user.DuplicateCredentialsException;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.service.VoucherService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

@SpringBootTest
public class CreateEventServiceImplTest {

  @Mock
  private EventRepository eventRepository;
  @Mock
  private UsersService usersService;
  @Mock
  private CategoryRepository categoryRepository;
  @Mock
  private TicketTypeService ticketTypeService;
  @Mock
  private ImageService imageService;
  @Mock
  private VoucherService voucherService;

  @InjectMocks
  private CreateEventServiceImpl createEventService = new CreateEventServiceImpl(eventRepository, usersService, categoryRepository, ticketTypeService, imageService, voucherService);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    createEventService = new CreateEventServiceImpl(eventRepository, usersService, categoryRepository, ticketTypeService, imageService, voucherService);
  }

  @Test
  void createEvent_Success() {
    // Arrange
    CreateEventRequestDto requestDto = new CreateEventRequestDto();
    requestDto.setTitle("Test Event");
    requestDto.setLocation("Test Location");
    requestDto.setEventDate(LocalDate.now().plusWeeks(1));
    requestDto.setStartTime(LocalTime.NOON);
    requestDto.setTicketTypeRequestDtos(new HashSet<>());
    requestDto.setIsFree(false);
    requestDto.setCategoryId(1L);

    Users organizer = new Users();
    organizer.setIsOrganizer(true);

    Category category = new Category();
    category.setId(1L);

    when(usersService.getCurrentUser()).thenReturn(organizer);
    when(eventRepository.findByTitleAndLocationAndEventDateAndStartTime(anyString(), anyString(), any(), any()))
        .thenReturn(Optional.empty());
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

    // Act
    EventResponseDto result = createEventService.createEvent(requestDto);

    // Assert
    assertNotNull(result);
    verify(eventRepository, times(2)).save(any(Event.class));
  }

  @Test
  void createEvent_DuplicateEvent_ThrowsException() {
    // Arrange
    CreateEventRequestDto requestDto = new CreateEventRequestDto();
    requestDto.setTitle("Test Event");
    requestDto.setLocation("Test Location");
    requestDto.setEventDate(LocalDate.now().plusWeeks(1));
    requestDto.setStartTime(LocalTime.NOON);

    when(eventRepository.findByTitleAndLocationAndEventDateAndStartTime(anyString(), anyString(), any(), any()))
        .thenReturn(Optional.of(new Event()));

    // Act & Assert
    assertThrows(DuplicateCredentialsException.class, () -> createEventService.createEvent(requestDto));
  }

  @Test
  void createEvent_NonOrganizerUser_ThrowsException() {
    // Arrange
    CreateEventRequestDto requestDto = new CreateEventRequestDto();
    Users nonOrganizer = new Users();
    nonOrganizer.setIsOrganizer(false);

    when(usersService.getCurrentUser()).thenReturn(nonOrganizer);

    // Act & Assert
    assertThrows(AccessDeniedException.class, () -> createEventService.createEvent(requestDto));
  }

  @Test
  void createEvent_InvalidEventDate_ThrowsException() {
    // Arrange
    CreateEventRequestDto requestDto = new CreateEventRequestDto();
    requestDto.setEventDate(LocalDate.now());

    Users organizer = new Users();
    organizer.setIsOrganizer(true);

    when(usersService.getCurrentUser()).thenReturn(organizer);

    // Act & Assert
    assertThrows(EventDateInvalidException.class, () -> createEventService.createEvent(requestDto));
  }

  @Test
  void setCategory_CategoryNotFound_ThrowsException() {
    // Arrange
    Event event = new Event();
    CreateEventRequestDto requestDto = new CreateEventRequestDto();
    requestDto.setCategoryId(1L);

    when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(CategoryNotFoundException.class, () -> createEventService.setCategory(event, requestDto));
  }

  @Test
  void setCategory_Success() {
    // Arrange
    Event event = new Event();
    CreateEventRequestDto requestDto = new CreateEventRequestDto();
    requestDto.setCategoryId(1L);

    Category category = new Category();
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

    // Act
    createEventService.setCategory(event, requestDto);

    // Assert
    assertEquals(category, event.getCategory());
  }
}
