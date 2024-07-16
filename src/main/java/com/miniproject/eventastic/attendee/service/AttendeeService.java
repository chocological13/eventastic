package com.miniproject.eventastic.attendee.service;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.entity.AttendeeId;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.users.entity.Users;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttendeeService {

  void saveAttendee(Attendee attendee);

  Optional<Attendee> findAttendee(AttendeeId attendeeId);

  Page<Attendee> getAttendeesByEventOrganizer(Users organizer, Pageable pageable);

  Page<Event> findEventsByAttendee(Long userId, Pageable pageable);
}
