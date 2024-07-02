package com.miniproject.eventastic.event.service.impl;

import com.miniproject.eventastic.event.entity.Event;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecifications {

  // Method to filter by title
  public static Specification<Event> hasTitle(String title) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
  }

  // Method to filter by category
  public static Specification<Event> hasCategory(String category) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.toLowerCase()));
  }

  // Method to filter by locations
  public static Specification<Event> hasLocation(String location) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
  }

  // Upcoming event filter
  public static Specification<Event> isUpcoming() {
    return ((root, query, criteriaBuilder) -> {
      LocalDate today = LocalDate.now();
      return criteriaBuilder.greaterThan(root.get("eventDate"), today);
    }
    );
  }
}