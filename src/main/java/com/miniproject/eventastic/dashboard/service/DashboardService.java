package com.miniproject.eventastic.dashboard.service;

import com.miniproject.eventastic.dashboard.dto.AttendeeDashboardDto;
import com.miniproject.eventastic.dashboard.dto.DailyStatisticsDto;
import com.miniproject.eventastic.dashboard.dto.EventDashboardDto;
import com.miniproject.eventastic.dashboard.dto.EventStatisticsDto;
import com.miniproject.eventastic.dashboard.dto.MonthlyRevenueDto;
import com.miniproject.eventastic.dashboard.dto.OrganizerDashboardSummaryDto;
import com.miniproject.eventastic.dashboard.dto.TrxDashboardDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;

public interface DashboardService {

  Page<EventDashboardDto> getEventsBetweenDates(int page, int size, LocalDate startDate, LocalDate endDate);
  Page<EventStatisticsDto> getEventStatistics(int page, int size, String sortBy);
  Page<AttendeeDashboardDto> getAttendees(int page, int size);
  Page<TrxDashboardDto> getTrxs(int page, int size);
  List<MonthlyRevenueDto> getMonthlyRevenue(int year);
  List<DailyStatisticsDto> getDailyStatistics(LocalDate startDate, LocalDate endDate);
  OrganizerDashboardSummaryDto getOrganizerDashboardSummary();
}
