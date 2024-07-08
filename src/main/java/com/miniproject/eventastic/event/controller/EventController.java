package com.miniproject.eventastic.event.controller;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.event.DuplicateEventException;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.user.AttendeeNotFoundException;
import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitRequestDto;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitResponseDto;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateVoucherResponseDto;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/events")
public class EventController {

  private final EventService eventService;
  private final VoucherService voucherService;

  @PostMapping("/create")
  public ResponseEntity<Response<EventResponseDto>> createEvent(@Valid @RequestBody CreateEventRequestDto requestDto) {
    try {
      log.info("Attempting to create event: {} on date: {}", requestDto.getTitle(), requestDto.getEventDate());

      EventResponseDto responseDto = eventService.createEvent(requestDto);
      log.info("Event created successfully: {} by organizer: {} on date: {}", responseDto.getTitle(), responseDto.getOrganizer(), responseDto.getEventDate());
      return Response.successfulResponse(HttpStatus.CREATED.value(), "Event successfully created!", responseDto);

    } catch (DuplicateEventException e) {
      return Response.failedResponse(HttpStatus.CONFLICT.value(), e.getMessage(), null);
    }
  }

  @GetMapping("/{eventId}")
  public ResponseEntity<Response<EventResponseDto>> getEvent(@PathVariable Long eventId) {
    Event existingEvent = eventService.getEventById(eventId);
    if (existingEvent == null) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), "Event not found!", null);
    }
    EventResponseDto responseDto = new EventResponseDto(existingEvent);
    return Response.successfulResponse(HttpStatus.OK.value(), "Event successfully retrieved!", responseDto);
  }

  // * Display available vouchers for event
  @GetMapping("/{eventId}/vouchers")
  public ResponseEntity<Response<List<CreateVoucherResponseDto>>> getVouchers(@PathVariable Long eventId) {
    try {
      Event existingEvent = eventService.getEventById(eventId);
      if (existingEvent == null) {
        return Response.failedResponse(HttpStatus.NOT_FOUND.value(), "Event not found!", null);
      }
      List<Voucher> eventVoucher = voucherService.getEventVouchers(eventId);
      List<CreateVoucherResponseDto> createVoucherResponseDtos = eventVoucher.stream().map(CreateVoucherResponseDto::new).toList();
      return Response.successfulResponse(HttpStatus.FOUND.value(), "Displaying vouchers for " + existingEvent.getTitle(),
          createVoucherResponseDtos);
    } catch (EventNotFoundException e) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
  }

  // search, sort, pagination
  @GetMapping
  public ResponseEntity<Response<Map<String, Object>>> getAllEvents(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) String order,
      @RequestParam(required = false) String direction
  ) {
    Page<EventResponseDto> eventPage = eventService.getEvents(page, size, title, category, location, order, direction);
    return Response.responseMapper(HttpStatus.FOUND.value(), "Displaying events..", eventPage);
  }

  @GetMapping("/upcoming")
  public ResponseEntity<Response<Map<String, Object>>> getUpcomingEvents(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Page<EventResponseDto> eventPage = eventService.getUpcomingEvents(page, size);
    return Response.responseMapper(HttpStatus.OK.value(), "Listing upcoming events...", eventPage);
  }

  @PutMapping("/{eventId}/update")
  public ResponseEntity<Response<EventResponseDto>> updateEvent(@PathVariable Long eventId, @Valid @RequestBody UpdateEventRequestDto requestDto) {
    try {
      log.info("Attempting to update event: {}", eventId);
      EventResponseDto responseDto = eventService.updateEvent(eventId, requestDto);
      return Response.successfulResponse(HttpStatus.OK.value(), "Event successfully updated!", responseDto);
    } catch (EventNotFoundException e) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
  }

  @DeleteMapping("/{eventId}")
  public ResponseEntity<Response<EventResponseDto>> deleteEvent(@PathVariable Long eventId) {
    try {
      log.info("Attempting to delete event: {}", eventId);
      eventService.deleteEvent(eventId);
      return Response.successfulResponse(HttpStatus.OK.value(), "Event successfully deleted!", null);
    } catch (EventNotFoundException e) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
  }

  @PostMapping("/{eventId}/reviews")
  public ResponseEntity<Response<ReviewSubmitResponseDto>> submitReview(@PathVariable Long eventId,
      @RequestBody ReviewSubmitRequestDto requestDto) {
    try {
      Review review = eventService.submitReview(eventId, requestDto);
      return Response.successfulResponse(HttpStatus.CREATED.value(), "Review posted!",
          new ReviewSubmitResponseDto(review));
    } catch (EventNotFoundException | AttendeeNotFoundException e) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
  }
}
