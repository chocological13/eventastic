package com.miniproject.eventastic.review.service.impl;

import com.miniproject.eventastic.exceptions.event.ReviewNotFoundException;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitResponseDto;
import com.miniproject.eventastic.review.repository.ReviewRepository;
import com.miniproject.eventastic.review.service.ReviewService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final ReviewRepository reviewRepository;

  @Override
  public void saveReview(Review review) {
    reviewRepository.save(review);
  }

  @Override
  public Page<ReviewSubmitResponseDto> getReviewsByEventId(Long eventId, Pageable pageable) throws ReviewNotFoundException {
    Page<Review> reviews = reviewRepository.findByEventId(eventId, pageable);
    if (reviews == null) {
      throw new ReviewNotFoundException("This event has no reviews yet");
    } else return reviews.map(ReviewSubmitResponseDto::new);
  }
}
