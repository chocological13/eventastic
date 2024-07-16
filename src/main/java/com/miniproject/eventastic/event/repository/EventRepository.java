package com.miniproject.eventastic.event.repository;

import com.miniproject.eventastic.dashboard.dto.EventStatisticsDto;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.users.entity.Users;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

  Page<Event> findByOrganizerAndEventDateBetween(Users organizer, LocalDate startDate, LocalDate endDate, Pageable pageable);

  Optional<Event> findByTitleAndLocationAndEventDateAndStartTime(String title, String location, LocalDate eventDate,
      LocalTime startTime);

  @Query(
      """
             SELECT new com.miniproject.eventastic.dashboard.dto.EventStatisticsDto(
                e.id,
                e.title,
                COUNT(DISTINCT a.id.userId),
                COUNT(DISTINCT t.user.id),
                SUM(t.totalAmount),
                SUM(t.qty),
                AVG(tt.price)
              )
              FROM Event e
              LEFT JOIN e.attendees a
              LEFT JOIN e.trxes t
              LEFT JOIN e.ticketTypes tt
              WHERE e.organizer = :organizer
              GROUP BY e.id, e.title
          """
  )
  Page<EventStatisticsDto> getEventStatisticsDto(@Param("organizer") Users organizer, Pageable pageable);
}
