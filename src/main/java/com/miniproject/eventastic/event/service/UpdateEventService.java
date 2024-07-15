package com.miniproject.eventastic.event.service;

import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;

public interface UpdateEventService {

  EventResponseDto updateEvent(Long eventId, UpdateEventRequestDto requestDto);

}
