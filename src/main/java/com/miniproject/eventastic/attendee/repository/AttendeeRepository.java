package com.miniproject.eventastic.attendee.repository;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.entity.AttendeeId;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.users.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, AttendeeId> {

}
