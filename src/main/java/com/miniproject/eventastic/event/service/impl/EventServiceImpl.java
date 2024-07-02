package com.miniproject.eventastic.event.service.impl;

import static com.miniproject.eventastic.event.entity.dto.EventResponseDto.toEventResponseDto;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.EventExistsException;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.entity.dto.TicketTypeRequestDto;
import com.miniproject.eventastic.ticketType.repository.TicketTypeRepository;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import java.math.BigDecimal;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final TicketTypeRepository ticketTypeRepository;
  private final UsersService usersService;

  @Override
  public EventResponseDto createEvent(CreateEventRequestDto requestDto) {
    CreateEventRequestDto e = new CreateEventRequestDto();
    Event createdEvent = e.dtoToEvent(requestDto);

    // check if there's a duplicate
    if (isDuplicateEvent(requestDto)) {
      throw new EventExistsException("Event already exists. Please create another one.");
    }

    // extract user
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String organizerName = auth.getName();
    Users organizer = usersService.getByUsername(organizerName);
    createdEvent.setOrganizer(organizer);

    // save event here, so we can set it to the ticket types
    eventRepository.save(createdEvent);

    // init ticket type
    Set<TicketTypeRequestDto> ticketTypeRequestDtos = requestDto.getTicketTypeRequestDtos();
    Set<TicketType> ticketTypes = new HashSet<>();

    if (!createdEvent.getIsFree()) {
      // map ticket type
      for (TicketTypeRequestDto ticketTypeRequestDto : ticketTypeRequestDtos) {
        TicketType ticketType = TicketTypeRequestDto.requestToTicketTypeEntity(ticketTypeRequestDto);
        ticketType.setEvent(createdEvent); // Associate with the created Event
        ticketType.setAvailableSeat(ticketTypeRequestDto.getSeatLimit());

        ticketTypes.add(ticketType);
        ticketTypeRepository.save(ticketType);
      }
    } else {

      // default ticket type if isFree is true
      TicketType freeTicketType = new TicketType();
      TicketTypeRequestDto firstInSet = ticketTypeRequestDtos.iterator().next();

      freeTicketType.setName("Free Admission");
      freeTicketType.setDescription("Free entry ticket");
      freeTicketType.setPrice(BigDecimal.ZERO);
      freeTicketType.setSeatLimit(firstInSet.getSeatLimit());
      freeTicketType.setAvailableSeat(firstInSet.getSeatLimit());
      freeTicketType.setEvent(createdEvent);

      ticketTypes.add(freeTicketType);
      ticketTypeRepository.save(freeTicketType);
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

    EventResponseDto responseDto = new EventResponseDto();
    return toEventResponseDto(createdEvent);
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
    return eventsPage.map(EventResponseDto::toEventResponseDto);
  }

  @Override
  public Page<EventResponseDto> getUpcomingEvents(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());

    Specification<Event> specification = EventSpecifications.isUpcoming();

    Page<Event> eventsPage = eventRepository.findAll(specification, pageable);
    return eventsPage.map(EventResponseDto::toEventResponseDto);
  }

  @Override
  public EventResponseDto getSpecificEvent(Long eventId) {
    Optional<Event> optionalEvent = eventRepository.findById(eventId);
    return optionalEvent.map(EventResponseDto::toEventResponseDto).orElse(null);
  }


  @Override
  public Boolean isDuplicateEvent(CreateEventRequestDto checkDuplicate) {
    String title = checkDuplicate.getTitle();
    String location = checkDuplicate.getLocation();
    LocalDate eventDate = checkDuplicate.getEventDate();
    LocalTime startTime = checkDuplicate.getStartTime();
    Optional<Event> checkEvent = eventRepository.findByTitleAndLocationAndEventDateAndStartTime(title, location, eventDate, startTime);
    if (checkEvent.isPresent()) {
      return true;
    } else {
      return false;
    }
  }

}

class EventSpecifications {

  // Method to filter by title
  public static Specification<Event> hasTitle(String title) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
  }

  // Method to filter by category
  public static Specification<Event> hasCategory(String category) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.toLowerCase()));
  }

  // Method to filter by locations
  public static Specification<Event> hasLocation(String location) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
  }

  // Upcoming event filter
  public static Specification<Event> isUpcoming() {
    return ((root, query, criteriaBuilder) -> {
      LocalDate today = LocalDate.now();
      return criteriaBuilder.greaterThan(root.get("eventDate"), today);
    }
    );
  }
}
