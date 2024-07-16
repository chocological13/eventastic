package com.miniproject.eventastic.dashboard.service.impl;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.dashboard.dto.AttendeeDto;
import com.miniproject.eventastic.dashboard.dto.EventDashboardDto;
import com.miniproject.eventastic.dashboard.dto.EventStatisticsDto;
import com.miniproject.eventastic.dashboard.service.DashboardService;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final EventService eventService;
  private final UsersService usersService;
  private final AttendeeService attendeeService;

  @Override
  public Page<EventDashboardDto> getEventsBetweenDates(int page, int size, LocalDate startDate, LocalDate endDate) throws RuntimeException {
    if (startDate == null) {
      startDate = LocalDate.now();
    }
    Users organizer = usersService.getCurrentUser();
    Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
    Page<Event> eventsPage = eventService.findEventBetweenDates(organizer,startDate, endDate, pageable);

    return eventsPage.map(EventDashboardDto::new);
  }

  @Override
  @Transactional
  public EventStatisticsDto getEventStatistics(Long eventId) throws RuntimeException {
    // * validate organizer
    Users organizer = usersService.getCurrentUser();
    Event event = eventService.getEventById(eventId);
    if (event.getOrganizer() != organizer) {
      throw new AccessDeniedException("You do not have permission to access this resource");
    }

    EventStatisticsDto eventStatisticsDto = eventService.getEventStatistics(eventId);
    if (eventStatisticsDto == null) {
      throw new EventNotFoundException("You did not organize any event with this ID, please make sure you have "
                                       + "entered the right ID!");
    }
    return eventStatisticsDto;
  }

  @Override
  public Page<AttendeeDto> getAttendees(int page, int size) throws RuntimeException {
    Users organizer = usersService.getCurrentUser();
    Pageable pageable = PageRequest.of(page, size, Sort.by("attendingAt").ascending());
    Page<Attendee> attendees = attendeeService.getAttendeesByEventOrganizer(organizer, pageable);
    return attendees.map(AttendeeDto::new);
  }
}
