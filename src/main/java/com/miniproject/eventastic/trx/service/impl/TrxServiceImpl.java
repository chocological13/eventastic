package com.miniproject.eventastic.trx.service.impl;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.repository.EventRepository;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.event.EventEndedException;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.trx.InsufficientPointsException;
import com.miniproject.eventastic.exceptions.trx.NotAwardeeException;
import com.miniproject.eventastic.exceptions.trx.PaymentMethodNotFound;
import com.miniproject.eventastic.exceptions.trx.SeatUnavailableException;
import com.miniproject.eventastic.exceptions.trx.TicketTypeNotFoundException;
import com.miniproject.eventastic.exceptions.trx.VoucherInvalidException;
import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.ticket.service.TicketService;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import com.miniproject.eventastic.trx.entity.Payment;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.trx.entity.dto.PurchaseRequestDto;
import com.miniproject.eventastic.trx.repository.PaymentRepository;
import com.miniproject.eventastic.trx.repository.TrxRepository;
import com.miniproject.eventastic.trx.service.TrxService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
public class TrxServiceImpl implements TrxService {

  private final TrxRepository trxRepository;
  private final PaymentRepository paymentRepository;
  private final EventService eventService;
  private final TicketTypeService ticketTypeService;
  private final PointsWalletService pointsWalletService;
  private final VoucherService voucherService;
  private final UsersService usersService;
  private final EventRepository eventRepository;
  private final TicketService ticketService;


  @Override
  @Transactional
  public Trx purchaseTicket(PurchaseRequestDto requestDto) {
    // * get logged-in user
    Users loggedUser = usersService.getCurrentUser();

    // * check validity of event
    Event event = validateAndRetrieveEvent(requestDto.getEventId());

    // * check ticket type and available seat
    TicketType ticketType = validateAndRetrieveTicketType(requestDto.getTicketTypeId());

    // * get trx
    Trx trx = createTransaction(requestDto, loggedUser, event, ticketType);

    usePoints(requestDto, trx);
    useVoucher(requestDto, trx);
    setPaymentMethod(requestDto, trx);

    trx.setTrxDate(Instant.now());
    trxRepository.save(trx);

    return trx;
  }

  // * utilities
  // Event
  public Event validateAndRetrieveEvent(Long eventId) {
    Event event = eventService.getEventById(eventId);
    if (event == null) {
      throw new EventNotFoundException("Event not found!");
    }
    if (event.getEventDate().isBefore(LocalDate.now())) {
      throw new EventEndedException("Event has ended. Please choose another event.");
    }
    return event;
  }

  // TicketType
  public TicketType validateAndRetrieveTicketType(Long ticketTypeId) {
    TicketType ticketType = ticketTypeService.getTicketTypeById(ticketTypeId);
    if (ticketType == null) {
      throw new TicketTypeNotFoundException("This ticket type does not exist!");
    }
    if (ticketType.getAvailableSeat() <= 0) {
      throw new SeatUnavailableException("Seat for this ticket type is sold out!");
    }
    return ticketType;
  }

  // Trx = requestDto, loggedUser, event, ticketType
  public Trx createTransaction(PurchaseRequestDto requestDto, Users loggedUser, Event event, TicketType ticketType) {
    Trx trx = new Trx();
    trx.setUser(loggedUser);
    trx.setEvent(event);
    trx.setTicketType(ticketType);
    trx.setQty(requestDto.getQty());
    trx.setInitialAmount(calculateInitialAmount(ticketType, requestDto.getQty()));

    Set<Ticket> ticketSet = generateAndSaveTickets(loggedUser, ticketType, requestDto.getQty());
    trx.setTickets(ticketSet);

    decrementAvailableSeat(event, ticketType, requestDto.getQty());
    return trx;
  }

  // BigDecimal calcInitialPrice = ticketType,
  public BigDecimal calculateInitialAmount(TicketType ticketType, Integer qty) {
    return ticketType.getPrice().multiply(BigDecimal.valueOf(qty));
  }

  // Set<Tickets> generate and save tickets
  public Set<Ticket> generateAndSaveTickets(Users loggedUser, TicketType ticketType, Integer qty) {
    Set<Ticket> ticketSet = new LinkedHashSet<>();
    for (int i = 0; i < qty; i++) {
      Ticket ticket = ticketService.generateTicket(loggedUser, ticketType);
      ticketService.saveTicket(ticket);
      ticketSet.add(ticket);
    }
    return ticketSet;
  }

  // decrement avail seats
  public void decrementAvailableSeat(Event event, TicketType ticketType, Integer qty) {
    event.setAvailableSeat(event.getAvailableSeat() - qty);
    eventRepository.save(event);

    ticketType.setAvailableSeat(event.getAvailableSeat() - qty);
    ticketTypeService.saveTicketType(ticketType);
  }

  // processPointsUsage calls applypoints
  public void usePoints(PurchaseRequestDto requestDto, Trx trx) {
    if (requestDto.getUsingPoints()) {
      PointsWallet pointsWallet = pointsWalletService.getPointsWallet(trx.getUser());
      if (pointsWallet.getPoints() <= 0) {
        throw new InsufficientPointsException("Insufficient points to be used :(");
      } else {
        applyPoints(trx, pointsWallet);
      }
      trx.setPointsWallet(pointsWallet);
    }
  }

  // apply points
  public void applyPoints(Trx trx, PointsWallet pointsWallet) {
    BigDecimal points = BigDecimal.valueOf(pointsWallet.getPoints());
    BigDecimal price = trx.getInitialAmount();

    if (points.compareTo(price) < 0) {
      trx.setTotalAmount(trx.getInitialAmount().subtract(BigDecimal.valueOf(pointsWallet.getPoints())));
      pointsWallet.setPoints(0);
    } else if (points.compareTo(price) > 0) {
      int endPoints = points.subtract(price).setScale(0, RoundingMode.HALF_UP).intValue();
      trx.setTotalAmount(BigDecimal.ZERO);
      pointsWallet.setPoints(endPoints);
    } else {
      throw new InsufficientPointsException("Insufficient points to be used !!");
    }
    pointsWalletService.savePointsWallet(pointsWallet);
  }

  // processVVoucherUsage -> applyvoucher
  public void useVoucher(PurchaseRequestDto requestDto, Trx trx) {
    if (requestDto.getVoucherCode() != null) {
      Voucher voucher = voucherService.getVoucher(requestDto.getVoucherCode());
      if (voucher == null) {
        throw new VoucherNotFoundException("This voucher does not exist!");
      }
      if (voucher.getExpiresAt().isBefore(Instant.now())) {
        throw new VoucherInvalidException("Voucher is expired!");
      }
      if (voucher.getUseLimit() <= 0) {
        throw new VoucherInvalidException("Voucher has been all used up!");
      }
      trx.setVoucher(voucher);
      applyVoucher(trx, voucher);
    }
  }

  // applyvoucher
  public void applyVoucher(Trx trx, Voucher voucher) {
    Users user = trx.getUser();
    Users awardee = voucher.getAwardee();
    if (awardee == null || awardee.equals(user)) {
      BigDecimal discount = trx.getInitialAmount()
          .multiply(BigDecimal.valueOf(voucher.getPercentDiscount()).divide(BigDecimal.valueOf(100)));

      trx.setTotalAmount(trx.getInitialAmount().subtract(discount));
      voucher.setUseLimit(voucher.getUseLimit() - 1);
      voucherService.saveVoucher(voucher);
    } else {
      throw new NotAwardeeException("This voucher was not meant for you >:C");
    }
  }

  // set payment method
  private void setPaymentMethod(PurchaseRequestDto requestDto, Trx trx) {
    Payment payment = paymentRepository.findById(requestDto.getPaymentId()).orElseThrow(() ->
        new PaymentMethodNotFound("Please enter a valid method of payment!"));
    trx.setPayment(payment);
  }

}
