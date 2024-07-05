package com.miniproject.eventastic.attendee.repository;

import com.miniproject.eventastic.attendee.entity.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, Long> {

}
