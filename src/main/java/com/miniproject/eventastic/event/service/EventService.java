package com.miniproject.eventastic.event.service;

import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitRequestDto;
import java.util.Set;
import org.springframework.data.domain.Page;

public interface EventService {

  void saveEvent(Event event);

  EventResponseDto createEvent(CreateEventRequestDto requestDto);

  Page<EventResponseDto> getEvents(int page, int size, String title, String category, String location, String order,
      String direction);

  Page<EventResponseDto> getUpcomingEvents(int page, int size);

  Event getEventById(Long eventId);

  EventResponseDto updateEvent(Long eventId, UpdateEventRequestDto requestDto);

  // soft delete
  void deleteEvent(Long eventId);

  // category
  Category getCategoryById(Long eventId);

  // review
  Review submitReview(Long eventId, ReviewSubmitRequestDto requestDto);

  // display event's review
  Set<Review> getEventReviews(Long eventId);
}
