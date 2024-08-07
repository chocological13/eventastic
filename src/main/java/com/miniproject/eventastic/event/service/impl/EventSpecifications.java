package com.miniproject.eventastic.event.service.impl;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.users.entity.Users;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
    return ((root, query, criteriaBuilder) -> {
      Join<Event, Category> categoryJoin = root.join("category", JoinType.INNER);
      return criteriaBuilder.equal(criteriaBuilder.lower(categoryJoin.get("name")), category.toLowerCase());
    });
  }

  // Method to filter by locations
  public static Specification<Event> hasLocation(String location) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
  }

  public static Specification<Event> hasOrganizer(String organizer) {
    return ((root, query, criteriaBuilder) -> {
      Join<Event, Users> organizerJoin = root.join("organizer", JoinType.INNER);
      return criteriaBuilder.equal(criteriaBuilder.lower(organizerJoin.get("username")), organizer.toLowerCase());
    });
  }

  public static Specification<Event> hasKeyword(String keyword) {
    return (root, query, criteriaBuilder) -> {
      String likePattern = "%" + keyword.toLowerCase() + "%";
      Join<Event, Users> organizerJoin = root.join("organizer", JoinType.INNER);
      Join<Event, Category> categoryJoin = root.join("category", JoinType.INNER);
      return criteriaBuilder.or(
          criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("venue")), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("name")), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(organizerJoin.get("username")), likePattern)
      );
    };
  }

  public static Specification<Event> isFree(Boolean isFree) {
    return (root, query, criteriaBuilder) -> {
      return criteriaBuilder.equal(root.get("isFree"), isFree);
    };
  }

  // Upcoming event filter
  public static Specification<Event> isUpcoming() {
    return ((root, query, criteriaBuilder) -> {
      LocalDate today = LocalDate.now();
      return criteriaBuilder.greaterThan(root.get("eventDate"), today);
    }
    );
  }

  // Organizer
  public static Specification<Event> byOrganizerId(Long organizerId) {
    return ((root, query, criteriaBuilder) -> {
      Join<Event, Users> organizerJoin = root.join("organizer", JoinType.INNER);
      return criteriaBuilder.equal(organizerJoin.get("id"), organizerId);
    });
  }
}
