package com.miniproject.eventastic.review.repository;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.users.entity.Users;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
  Page<Review> findByEventId(Long eventId, Pageable pageable);
  Optional<Review> findByReviewerAndEvent(Users reviewer, Event event);
}
