package com.miniproject.eventastic.event.service;

import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;

public interface CreateEventService {

  EventResponseDto createEvent(CreateEventRequestDto requestDto);
}
