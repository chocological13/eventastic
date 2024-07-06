package com.miniproject.eventastic.attendee.service;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.entity.AttendeeId;
import java.util.Optional;

public interface AttendeeService {

  void saveAttendee(Attendee attendee);

  Optional<Attendee> findAttendee(AttendeeId attendeeId);
}
