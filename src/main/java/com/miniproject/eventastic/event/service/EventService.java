package com.miniproject.eventastic.event.service;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Page;

public interface EventService {

  EventResponseDto createEvent(CreateEventRequestDto requestDto);

  Page<EventResponseDto> getEvents(int page, int size, String title, String category, String location, String order,
      String direction);

  Page<EventResponseDto> getUpcomingEvents(int page, int size);

  EventResponseDto getSpecificEvent(Long eventId);

  Event getEventById(Long eventId);

  Boolean isDuplicateEvent(CreateEventRequestDto checkDuplicate);

  EventResponseDto updateEvent(Long eventId, UpdateEventRequestDto requestDto);

  // soft delete
  void deleteEvent(Long eventId);
}
