package com.miniproject.eventastic.ticket.repository;

import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.users.entity.Users;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

  Set<Ticket> findByUser(Users user);
}
