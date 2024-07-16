package com.miniproject.eventastic.dashboard.service.impl;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.dashboard.dto.AttendeeDashboardDto;
import com.miniproject.eventastic.dashboard.dto.DailyStatisticsDto;
import com.miniproject.eventastic.dashboard.dto.EventDashboardDto;
import com.miniproject.eventastic.dashboard.dto.EventStatisticsDto;
import com.miniproject.eventastic.dashboard.dto.MonthlyRevenueDto;
import com.miniproject.eventastic.dashboard.dto.OrganizerDashboardSummaryDto;
import com.miniproject.eventastic.dashboard.dto.TrxDashboardDto;
import com.miniproject.eventastic.dashboard.service.DashboardService;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.trx.service.TrxService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final EventService eventService;
  private final UsersService usersService;
  private final AttendeeService attendeeService;
  private final TrxService trxService;

  @Override
  public Page<EventDashboardDto> getEventsBetweenDates(int page, int size, LocalDate startDate, LocalDate endDate) throws RuntimeException {
    if (startDate == null) {
      startDate = LocalDate.now();
    }
    Users organizer = usersService.getCurrentUser();
    Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
    Page<Event> eventsPage = eventService.getEventBetweenDates(organizer,startDate, endDate, pageable);

    return eventsPage.map(EventDashboardDto::new);
  }

  @Override
  @Transactional
  public Page<EventStatisticsDto> getEventStatistics(int page, int size, String sortBy) throws RuntimeException {
    // * validate organizer
    Users organizer = usersService.getCurrentUser();

    // * set up pageable
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());

    return eventService.getEventStatistics(organizer, pageable);
  }

  @Override
  public Page<AttendeeDashboardDto> getAttendees(int page, int size) throws RuntimeException {
    Users organizer = usersService.getCurrentUser();
    Pageable pageable = PageRequest.of(page, size, Sort.by("attendingAt").ascending());
    Page<Attendee> attendees = attendeeService.getAttendeesByEventOrganizer(organizer, pageable);
    return attendees.map(AttendeeDashboardDto::new);
  }

  @Override
  public Page<TrxDashboardDto> getTrxs(int page, int size) throws RuntimeException {
    Users organizer = usersService.getCurrentUser();
    Pageable pageable = PageRequest.of(page, size, Sort.by("event").ascending());
    Page<Trx> trxesPage = trxService.getTrxsByOrganizer(organizer, pageable);
    return trxesPage.map(TrxDashboardDto::new);
  }

  @Override
  public List<MonthlyRevenueDto> getMonthlyRevenue(int year) throws RuntimeException {
    Users organizer = usersService.getCurrentUser();
    return eventService.getMonthlyRevenue(organizer, year);
  }

  @Override
  public List<DailyStatisticsDto> getDailyStatistics(LocalDate startDate, LocalDate endDate) throws RuntimeException {
    Users organizer = usersService.getCurrentUser();
    return trxService.getDailyStatisticse(organizer, startDate, endDate);
  }

  @Override
  public OrganizerDashboardSummaryDto getOrganizerDashboardSummary() {
    Users organizer = usersService.getCurrentUser();
    return eventService.getOrganizerDashboardSummary(organizer);
  }
}
