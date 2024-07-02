package com.miniproject.eventastic.event.repository;

import com.miniproject.eventastic.event.entity.Event;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
  Optional<Event> findByTitleAndEventDate(String title, LocalDate eventDate);
  Optional<Event> findByTitleAndLocationAndEventDateAndStartTime(String title, String location, LocalDate eventDate,
      LocalTime startTime);
}
