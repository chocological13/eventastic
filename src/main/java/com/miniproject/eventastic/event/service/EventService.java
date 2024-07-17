package com.miniproject.eventastic.event.service;

import com.miniproject.eventastic.dashboard.dto.EventStatisticsDto;
import com.miniproject.eventastic.dashboard.dto.EventSummaryDto;
import com.miniproject.eventastic.dashboard.dto.MonthlyRevenueDto;
import com.miniproject.eventastic.dashboard.dto.OrganizerDashboardSummaryDto;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.image.entity.ImageEvent;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitRequestDto;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitResponseDto;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateEventVoucherRequestDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {

  void saveEvent(Event event);

  Page<EventResponseDto> getEvents(int page, int size, String title, String category, String location,
      String organizer, Boolean isFree, String order, String direction);

  Page<EventResponseDto> getByKeyword(int page, int size, String keyword);

  Page<EventResponseDto> getUpcomingEvents(int page, int size);

  Event getEventById(Long eventId);

  // utilities
  void deleteEvent(Long eventId);

  // Region - other entities service calls

  // create voucher
  Voucher createEventVoucher(Long eventId, CreateEventVoucherRequestDto requestDto);

  // category
  Category getCategoryById(Long eventId);

  // review
  Review submitReview(Long eventId, ReviewSubmitRequestDto requestDto);

  // display event's review
  Page<ReviewSubmitResponseDto> getEventReviews(Long eventId, int page, int size);

  // upload image for events
  ImageEvent uploadEventImage(ImageUploadRequestDto requestDto);

  // Region - Dashboard
  Page<EventStatisticsDto> getEventStatistics(Users organizer, Pageable pageable);

  Page<Event> getEventBetweenDates(Users organizer, LocalDate startDate, LocalDate endDate, Pageable pageable);

  List<MonthlyRevenueDto> getMonthlyRevenue(Users organizer, Integer year);

  OrganizerDashboardSummaryDto getOrganizerDashboardSummary(Users organizer);

  List<EventSummaryDto> getEventSummary(Users organizer);
}
