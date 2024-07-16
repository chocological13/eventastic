package com.miniproject.eventastic.dashboard.controller;

import com.miniproject.eventastic.dashboard.service.DashboardService;
import com.miniproject.eventastic.responses.Response;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/events")
  public ResponseEntity<Response<Map<String, Object>>> getEvents(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam LocalDate endDate
  ) {
    return Response.responseMapper(HttpStatus.OK.value(), "Here are your events!",
        dashboardService.getEventsBetweenDates(page, size, startDate, endDate));
  }

  @GetMapping("/statistics/events")
  public ResponseEntity<Response<Map<String, Object>>> getEventStatistics(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int  size,
      @RequestParam(defaultValue = "eventDate") String sortBy
      ) {
    return Response.responseMapper(HttpStatus.OK.value(), "Displaying statistics for your event!",
        dashboardService.getEventStatistics(page, size, sortBy));
  }

  @GetMapping("/attendees")
  public ResponseEntity<Response<Map<String, Object>>> getAttendees(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return Response.responseMapper(HttpStatus.OK.value(), "Here are your attendees!",
        dashboardService.getAttendees(page, size));
  }

}
