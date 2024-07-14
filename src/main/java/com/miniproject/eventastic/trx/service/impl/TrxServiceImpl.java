package com.miniproject.eventastic.trx.service.impl;

import com.miniproject.eventastic.exceptions.trx.InsufficientPointsException;
import com.miniproject.eventastic.exceptions.trx.NotAwardeeException;
import com.miniproject.eventastic.exceptions.trx.PaymentMethodNotFoundException;
import com.miniproject.eventastic.exceptions.trx.PointsWalletNotFoundException;
import com.miniproject.eventastic.exceptions.trx.SeatUnavailableException;
import com.miniproject.eventastic.exceptions.trx.TicketNotFoundException;
import com.miniproject.eventastic.exceptions.trx.TicketTypeNotFoundException;
import com.miniproject.eventastic.exceptions.trx.VoucherInvalidException;
import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
import com.miniproject.eventastic.exceptions.user.UserNotFoundException;
import com.miniproject.eventastic.mail.service.MailService;
import com.miniproject.eventastic.mail.service.entity.dto.MailTemplate;
import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.ticket.service.TicketService;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.trx.entity.dto.TrxPurchaseRequestDto;
import com.miniproject.eventastic.trx.event.TicketPurchasedEvent;
import com.miniproject.eventastic.trx.service.TrxService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
@Transactional
public class TrxServiceImpl implements TrxService {

  private final UsersService usersService;
  private final TicketService ticketService;
  private final ApplicationEventPublisher eventPublisher;
  private final MailService mailService;

  @Override
  @Transactional
  public Trx purchaseTicket(TrxPurchaseRequestDto requestDto) throws UserNotFoundException, AccessDeniedException,
      TicketTypeNotFoundException, PointsWalletNotFoundException, InsufficientPointsException, VoucherNotFoundException, VoucherInvalidException, SeatUnavailableException, NotAwardeeException, PaymentMethodNotFoundException {
    // * get logged-in user
    Users loggedUser = usersService.getCurrentUser();
    Trx trx = new Trx();
    eventPublisher.publishEvent(new TicketPurchasedEvent(this, loggedUser, trx, requestDto));

    // * send email confirmation
    sendConfirmationEmail(trx);
    return trx;
  }

  public void sendConfirmationEmail(Trx trx) {
    MailTemplate temp = new MailTemplate();
    temp = temp.buildPurchaseTemp(trx);
    // ! TODO : uncomment in production, suspend email sending for local
//    mailService.sendEmail(temp);
  }

  @Override
  public Set<Ticket> getUserTickets() throws TicketNotFoundException {
    Users loggedUser = usersService.getCurrentUser();
    Set<Ticket> ticketSet = ticketService.findTicketsByUser(loggedUser);
    if (ticketSet.isEmpty()) {
      throw new TicketNotFoundException("You have not purchased anything as of late :(");
    } else {
      return ticketSet;
    }
  }
}
