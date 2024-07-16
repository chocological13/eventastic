package com.miniproject.eventastic.dashboard.service;

import com.miniproject.eventastic.dashboard.dto.AttendeeDto;
import com.miniproject.eventastic.dashboard.dto.EventDashboardDto;
import com.miniproject.eventastic.dashboard.dto.EventStatisticsDto;
import java.time.LocalDate;
import org.springframework.data.domain.Page;

public interface DashboardService {

  Page<EventDashboardDto> getEventsBetweenDates(int page, int size, LocalDate startDate, LocalDate endDate);
  EventStatisticsDto getEventStatistics(Long eventId);
  Page<AttendeeDto> getAttendees(int page, int size);
}
