package com.miniproject.eventastic.event.service;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;
import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.image.entity.ImageEvent;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitRequestDto;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitResponseDto;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateEventVoucherRequestDto;
import org.springframework.data.domain.Page;

public interface EventService {

  void saveEvent(Event event);

  Page<EventResponseDto> getEvents(int page, int size, String title, String category, String location,
      String organizer, Boolean isFree, String order, String direction);

  Page<EventResponseDto> getUpcomingEvents(int page, int size);

  Page<EventResponseDto> getEventsByOrganizer(Long organizerId, int page, int size);

  Event getEventById(Long eventId);

  // soft delete
  void deleteEvent(Long eventId);

  // Region - other entities service calls

  // create voucher
  Voucher createEventVoucher(Long eventId, CreateEventVoucherRequestDto requestDto);

  // category
  Category getCategoryById(Long eventId);

  // review
  Review submitReview(Long eventId, ReviewSubmitRequestDto requestDto);

  // display event's review
  // TODO : give this pagination
  Page<ReviewSubmitResponseDto> getEventReviews(Long eventId, int page, int size);

  // upload image for events
  ImageEvent uploadEventImage(ImageUploadRequestDto requestDto);

}
