package com.miniproject.eventastic.attendee.service.impl;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.entity.AttendeeId;
import com.miniproject.eventastic.attendee.repository.AttendeeRepository;
import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.user.AttendeeNotFoundException;
import com.miniproject.eventastic.users.entity.Users;
import java.util.Optional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  @Override
  public Page<Attendee> getAttendeesByEventOrganizer(Users organizer, Pageable pageable) {
    Page<Attendee> attendeesPage = attendeeRepository.findAttendeeByEvent_Organizer(organizer, pageable);
    if (attendeesPage.isEmpty()) {
      throw new AttendeeNotFoundException("No one bought any ticket to your events yet, sadge :(");
    }
    return attendeesPage;
  }

  @Override
  public Page<Event> findEventsByAttendee(Long userId, Pageable pageable) throws EventNotFoundException {
    Page<Event> usersEvents = attendeeRepository.findEventsByUserId(userId, pageable);
    if (usersEvents.isEmpty()) {
      throw new EventNotFoundException("You have no events associated to you");
    } else return usersEvents;
  }
}
