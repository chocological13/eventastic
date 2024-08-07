package com.miniproject.eventastic.ticket.service;

import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.users.entity.Users;
import java.util.Set;

public interface TicketService {
    void saveTicket(Ticket ticket);

    Ticket generateTicket(Users user, TicketType ticketType);

    Set<Ticket> findTicketsByUser(Users user);
}
