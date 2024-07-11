package com.miniproject.eventastic.attendee.repository;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.entity.AttendeeId;
import com.miniproject.eventastic.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, AttendeeId> {

  @Query("""
      SELECT DISTINCT a.event
      FROM Attendee a
      WHERE a.user.id=:userId
      ORDER BY a.event.eventDate asc
      """)
  Page<Event> findEventsByUserId(@Param("userId") Long userId, Pageable pageable);

}
