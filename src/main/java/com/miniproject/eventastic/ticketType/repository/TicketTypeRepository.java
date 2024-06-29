package com.miniproject.eventastic.ticketType.repository;

import com.miniproject.eventastic.ticketType.entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {

}
