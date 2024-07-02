package com.miniproject.eventastic.event.controller;

import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.event.service.impl.EventServiceImpl;
import com.miniproject.eventastic.exceptions.EventExistsException;
import com.miniproject.eventastic.responses.Response;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/events")
public class EventController {

  private final EventServiceImpl eventService;

  @PostMapping("/create")
  public ResponseEntity<Response<EventResponseDto>> createEvent(@Valid @RequestBody CreateEventRequestDto requestDto) {
    try {
      log.info("Attempting to create event: {} on date: {}", requestDto.getTitle(), requestDto.getEventDate());

      EventResponseDto responseDto = eventService.createEvent(requestDto);
      log.info("Event created successfully: {} by organizer: {} on date: {}", responseDto.getTitle(), responseDto.getOrganizer(), responseDto.getEventDate());
      return Response.successfulResponse(HttpStatus.CREATED.value(), "Event successfully created!", responseDto);

    } catch (EventExistsException e) {
      return Response.failedResponse(HttpStatus.CONFLICT.value(), e.getMessage(), null);
    }
  }

  @GetMapping("/{eventId}")
  public ResponseEntity<Response<EventResponseDto>> getEvent(@PathVariable Long eventId) {
    EventResponseDto response = eventService.getSpecificEvent(eventId);
    if (response == null) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), "Event not found!", null);
    }
    return Response.successfulResponse(HttpStatus.OK.value(), "Event successfully retrieved!", response);
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
}
