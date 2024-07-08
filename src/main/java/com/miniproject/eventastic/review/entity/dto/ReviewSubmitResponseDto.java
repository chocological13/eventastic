package com.miniproject.eventastic.review.entity.dto;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.users.entity.Users;
import java.time.Instant;
import lombok.Data;

@Data
public class ReviewSubmitResponseDto {

  private Long id;
  private String reviewer;
  private String event;
  private String organizer;
  private String reviewMsg;
  private Integer rating;
  private Instant submitDate;

  public ReviewSubmitResponseDto(Review review) {
    this.id = review.getId();
    this.reviewer = review.getReviewer().getUsername();
    this.event = review.getEvent().getTitle();
    this.organizer = review.getOrganizer().getUsername();
    this.rating = review.getRating();
    this.reviewMsg = review.getReview();
    this.submitDate = review.getCreatedAt();
  }

  public ReviewSubmitResponseDto toDto(Review review) {
    return new ReviewSubmitResponseDto(review);
  }
}
