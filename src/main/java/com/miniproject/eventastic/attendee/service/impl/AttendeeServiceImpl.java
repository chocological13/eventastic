package com.miniproject.eventastic.attendee.service.impl;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.entity.AttendeeId;
import com.miniproject.eventastic.attendee.repository.AttendeeRepository;
import com.miniproject.eventastic.attendee.service.AttendeeService;
import java.util.Optional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class AttendeeServiceImpl implements AttendeeService {

  private final AttendeeRepository attendeeRepository;

  @Override
  public void saveAttendee(Attendee attendee) {
    attendeeRepository.save(attendee);
  }

  @Override
  public Optional<Attendee> findAttendee(AttendeeId attendeeId) {
    return attendeeRepository.findById(attendeeId);
  }
}
