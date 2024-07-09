package com.miniproject.eventastic.review.service;

import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.review.entity.dto.ReviewSubmitRequestDto;
import java.util.Set;

public interface ReviewService {

 void saveReview(Review review);

 Set<Review> getReviewsByEventId(Long eventId);
}
