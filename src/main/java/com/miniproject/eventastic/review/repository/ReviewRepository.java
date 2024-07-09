package com.miniproject.eventastic.review.repository;

import com.miniproject.eventastic.review.entity.Review;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
  Set<Review> findByEventId(Long eventId);
}
