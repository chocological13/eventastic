package com.miniproject.eventastic.event.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;
import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.trx.TicketNotFoundException;
import com.miniproject.eventastic.exceptions.trx.TicketTypeNotFoundException;
import com.miniproject.eventastic.image.entity.ImageEvent;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.entity.dto.update.TicketTypeUpdateRequestDto;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

@SpringBootTest
public class UpdateEventServiceImplTest {

  @Mock
  private EventRepository eventRepository;
  @Mock
  private UsersService usersService;
  @Mock
  private ImageService imageService;
  @Mock
  private TicketTypeService ticketTypeService;

  @InjectMocks
  private UpdateEventServiceImpl updateEventService = new UpdateEventServiceImpl(eventRepository, usersService, imageService, ticketTypeService);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    updateEventService = new UpdateEventServiceImpl(eventRepository, usersService, imageService, ticketTypeService);
  }

  @Test
  void updateEvent_Success() {
    // Arrange
    UpdateEventRequestDto requestDto = new UpdateEventRequestDto();
    requestDto.setTitle("Updated Event");

    Category category = new Category();
    category.setId(1L);

    Event existingEvent = new Event();
    existingEvent.setId(1L);
    existingEvent.setTitle("Original Event");
    existingEvent.setCategory(category);
    existingEvent.setSeatLimit(100);
    existingEvent.setSeatAvailability(existingEvent.getSeatLimit());

    Users organizer = new Users();
    existingEvent.setOrganizer(organizer);

    when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
    when(usersService.getCurrentUser()).thenReturn(organizer);

    // Act
    EventResponseDto result = updateEventService.updateEvent(1L, requestDto);

    if (result == null) {
      Optional<Event> retrievedEvent = eventRepository.findById(1L);
      System.out.println("Retrieved event: " + retrievedEvent);

      Users currentUser = usersService.getCurrentUser();
      System.out.println("Current user: " + currentUser);

      // Add a breakpoint here and step through the updateEvent method
    }

    // Assert
    assertNotNull(result);
    assertEquals("Updated Event", result.getTitle());
    verify(eventRepository).save(existingEvent);
  }

  @Test
  void updateEvent_EventNotFound_ThrowsException() {
    // Arrange
    Long eventId = 1L;
    UpdateEventRequestDto requestDto = new UpdateEventRequestDto();


    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EventNotFoundException.class, () -> updateEventService.updateEvent(eventId, requestDto));
  }

  @Test
  void verifyOrganizer_NotOrganizer_ThrowsException() {
    // Arrange
    Event event = new Event();
    Users organizer = new Users();
    event.setOrganizer(organizer);

    Users differentUser = new Users();
    when(usersService.getCurrentUser()).thenReturn(differentUser);

    // Act & Assert
    assertThrows(AccessDeniedException.class, () -> updateEventService.verifyOrganizer(event));
  }

  @Test
  void setImage_Success() {
    // Arrange
    Event event = new Event();
    UpdateEventRequestDto requestDto = new UpdateEventRequestDto();
    requestDto.setImageId(1L);

    ImageEvent imageEvent = new ImageEvent();
    when(imageService.getEventImageById(1L)).thenReturn(imageEvent);

    // Act
    updateEventService.setImage(event, requestDto);

    // Assert
    assertEquals(imageEvent, event.getEventImage());
  }

  @Test
  void setTicketTypes_ExistingTicketType_Success() {
    // Arrange
    Event event = new Event();
    UpdateEventRequestDto requestDto = new UpdateEventRequestDto();

    TicketType existingTicketType = new TicketType();
    existingTicketType.setId(1L);
    Set<TicketType> existingTicketTypes = new HashSet<>();
    existingTicketTypes.add(existingTicketType);
    event.setTicketTypes(existingTicketTypes);

    TicketTypeUpdateRequestDto ticketTypeUpdateDto = new TicketTypeUpdateRequestDto();
    ticketTypeUpdateDto.setTicketTypeId(1L);
    ticketTypeUpdateDto.setDescription("Updated description");
    ticketTypeUpdateDto.setPrice(BigDecimal.TEN);
    ticketTypeUpdateDto.setSeatLimit(100);

    Set<TicketTypeUpdateRequestDto> ticketTypeUpdates = new HashSet<>();
    ticketTypeUpdates.add(ticketTypeUpdateDto);
    requestDto.setTicketTypeUpdates(ticketTypeUpdates);

    // Act
    updateEventService.setTicketTypes(event, requestDto);

    // Assert
    TicketType updatedTicketType = event.getTicketTypes().iterator().next();
    assertEquals("Updated description", updatedTicketType.getDescription());
    assertEquals(BigDecimal.TEN, updatedTicketType.getPrice());
    assertEquals(100, updatedTicketType.getSeatLimit());
    assertEquals(100, updatedTicketType.getSeatAvailability());
    verify(ticketTypeService).saveTicketType(updatedTicketType);
  }

  @Test
  void setTicketTypes_NonExistingTicketType_ThrowsException() {
    // Arrange
    Event event = new Event();
    UpdateEventRequestDto requestDto = new UpdateEventRequestDto();

    TicketTypeUpdateRequestDto ticketTypeUpdateDto = new TicketTypeUpdateRequestDto();
    ticketTypeUpdateDto.setTicketTypeId(1L);

    Set<TicketTypeUpdateRequestDto> ticketTypeUpdates = new HashSet<>();
    ticketTypeUpdates.add(ticketTypeUpdateDto);
    requestDto.setTicketTypeUpdates(ticketTypeUpdates);

    event.setTicketTypes(new HashSet<>());

    // Act & Assert
    assertThrows(TicketNotFoundException.class, () -> updateEventService.setTicketTypes(event, requestDto));
  }

  @Test
  void setTicketTypes_NoTicketTypeId_ThrowsException() {
    // Arrange
    Event event = new Event();
    UpdateEventRequestDto requestDto = new UpdateEventRequestDto();

    TicketTypeUpdateRequestDto ticketTypeUpdateDto = new TicketTypeUpdateRequestDto();
    // No ticket type ID set

    Set<TicketTypeUpdateRequestDto> ticketTypeUpdates = new HashSet<>();
    ticketTypeUpdates.add(ticketTypeUpdateDto);
    requestDto.setTicketTypeUpdates(ticketTypeUpdates);

    // Act & Assert
    assertThrows(TicketTypeNotFoundException.class, () -> updateEventService.setTicketTypes(event, requestDto));
  }
}
