package com.miniproject.eventastic.review.service.impl;

import com.miniproject.eventastic.exceptions.event.ReviewNotFoundException;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.review.repository.ReviewRepository;
import com.miniproject.eventastic.review.service.ReviewService;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
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
  public Set<Review> getReviewsByEventId(Long eventId) {
    Set<Review> reviews = reviewRepository.findByEventId(eventId);
    if (reviews == null) {
      throw new ReviewNotFoundException("This event has no reviews yet");
    } else return reviews;
  }
}