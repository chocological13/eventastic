package com.miniproject.eventastic.review.service;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitResponseDto;
import com.miniproject.eventastic.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

 void saveReview(Review review);

 Page<ReviewSubmitResponseDto> getReviewsByEventId(Long eventId, Pageable pageable);

 boolean hasSubmittedReview(Users reviewer, Event event);
}
