package com.miniproject.eventastic.event.service;

import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import org.springframework.data.domain.Page;

public interface EventService {

  EventResponseDto createEvent(CreateEventRequestDto requestDto);

  Page<EventResponseDto> getEvents(int page, int size, String title, String category, String location, String order,
      String direction);

  Page<EventResponseDto> getUpcomingEvents(int page, int size);
}
