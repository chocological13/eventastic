package com.miniproject.eventastic.event.controller;

import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.responses.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

  private final EventService eventService;

  @PostMapping("/create")
  public ResponseEntity<Response<EventResponseDto>> createEvent(@RequestBody CreateEventRequestDto requestDto) {
    EventResponseDto responseDto = eventService.createEvent(requestDto);
    return Response.successfulResponse(HttpStatus.OK.value(), "Event successfully created!", responseDto);
  }
}
