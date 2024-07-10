package com.miniproject.eventastic.trx.event.listener;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.entity.AttendeeId;
import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.event.EventEndedException;
import com.miniproject.eventastic.exceptions.trx.InsufficientPointsException;
import com.miniproject.eventastic.exceptions.trx.NotAwardeeException;
import com.miniproject.eventastic.exceptions.trx.PaymentMethodNotFoundException;
import com.miniproject.eventastic.exceptions.trx.SeatUnavailableException;
import com.miniproject.eventastic.exceptions.trx.VoucherInvalidException;
import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
import com.miniproject.eventastic.organizerWalletTrx.service.OrganizerWalletTrxService;
import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.ticket.service.TicketService;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.service.TicketTypeService;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.trx.entity.dto.TrxPurchaseRequestDto;
import com.miniproject.eventastic.trx.event.TicketPurchasedEvent;
import com.miniproject.eventastic.trx.metadata.Payment;
import com.miniproject.eventastic.trx.repository.PaymentRepository;
import com.miniproject.eventastic.trx.repository.TrxRepository;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketPurchasedEventListener {

  private final EventService eventService;
  private final TicketTypeService ticketTypeService;
  private final TicketService ticketService;
  private final PointsWalletService pointsWalletService;
  private final PointsTrxService pointsTrxService;
  private final VoucherService voucherService;
  private final PaymentRepository paymentRepository;
  private final AttendeeService attendeeService;
  private final TrxRepository trxRepository;
  private final OrganizerWalletTrxService organizerWalletTrxService;

  @EventListener
  @Transactional
  public void handleTicketPurchasedEvent(TicketPurchasedEvent event) {
    Users loggedUser = event.getUser();
    Trx trx = event.getTrx();
    TrxPurchaseRequestDto requestDto = event.getRequestDto();

    // Get event
    Event eventPurchase = validateAndRetrieveEvent(requestDto.getEventId());

    // Check TT and available seat
    TicketType ticketType = validateAndRetrieveTicketType(requestDto.getTicketTypeId());

    // Create Trx
    createTransaction(requestDto, loggedUser, eventPurchase, ticketType, trx);

    // points and voucher usage and payment method
    PointsTrx pointsTrx = usePoints(requestDto, trx);
    BigDecimal discount = useVoucher(requestDto, trx);
    setPaymentMethod(requestDto, trx);

    // get total amount
    BigDecimal points = pointsTrx != null ? BigDecimal.valueOf(-pointsTrx.getPoints()) : BigDecimal.ZERO;
    BigDecimal initAmount = calculateInitialAmount(ticketType, requestDto.getQty());
    BigDecimal toBeDeducted = points.add(discount);
    BigDecimal totalAmount = initAmount.subtract(toBeDeducted);
    trx.setTotalAmount(totalAmount);

    trx.setTrxDate(Instant.now());
    trx.setIsPaid(true);
    trxRepository.save(trx);

    // set attendee for this purchase
    setAttendee(loggedUser, eventPurchase, requestDto.getQty());

    // set trx to PointsTrx
    if (pointsTrx != null) pointsTrx.setTrx(trx);

    // send payout to organizer
    organizerWalletTrxService.sendPayout(trx);
  }

  private Event validateAndRetrieveEvent(Long eventId) {
    Event event = eventService.getEventById(eventId);
    if (event.getEventDate().isBefore(LocalDate.now())) {
      throw new EventEndedException("Event has ended. Please choose another event.");
    }
    return event;
  }

  private TicketType validateAndRetrieveTicketType(Long ticketTypeId) {
    TicketType ticketType = ticketTypeService.getTicketTypeById(ticketTypeId);
    if (ticketType.getAvailableSeat() <= 0) {
      throw new SeatUnavailableException("Seat for this ticket type is sold out!");
    }
    return ticketType;
  }

  private void createTransaction(TrxPurchaseRequestDto requestDto, Users loggedUser, Event event,
      TicketType ticketType, Trx trx) {
    trx.setUser(loggedUser);
    trx.setEvent(event);
    trx.setTicketType(ticketType);
    trx.setQty(requestDto.getQty());
    trx.setInitialAmount(calculateInitialAmount(ticketType, requestDto.getQty()));

    Set<Ticket> ticketSet = generateAndSaveTickets(loggedUser, ticketType, requestDto.getQty());
    trx.setTickets(ticketSet);

    decrementAvailableSeat(event, ticketType, requestDto.getQty());
  }

  private BigDecimal calculateInitialAmount(TicketType ticketType, int qty) {
    BigDecimal unitPrice = ticketType.getPrice();
    return unitPrice.multiply(BigDecimal.valueOf(qty)).stripTrailingZeros().setScale(0, RoundingMode.HALF_UP);
  }

  private Set<Ticket> generateAndSaveTickets(Users loggedUser, TicketType ticketType, int qty) {
    Set<Ticket> ticketSet = new LinkedHashSet<>();
    for (int i = 0; i < qty; i++) {
      Ticket ticket = ticketService.generateTicket(loggedUser, ticketType);
      ticketService.saveTicket(ticket);
      ticketSet.add(ticket);
    }
    return ticketSet;
  }

  private void decrementAvailableSeat(Event event, TicketType ticketType, int qty) {
    event.setAvailableSeat(event.getAvailableSeat() - qty);
    eventService.saveEvent(event);

    ticketType.setAvailableSeat(event.getAvailableSeat() - qty);
    ticketTypeService.saveTicketType(ticketType);
  }

  private PointsTrx usePoints(TrxPurchaseRequestDto requestDto, Trx trx) {PointsTrx pointsTrx;
    if (requestDto.getUsingPoints()) {
      PointsWallet pointsWallet = pointsWalletService.getPointsWallet(trx.getUser());
      pointsTrx = applyPoints(trx, pointsWallet);
      trx.setPointsWallet(pointsWallet);
    } else {
      return null;
    }
    return pointsTrx;
  }

  private PointsTrx applyPoints(Trx trx, PointsWallet pointsWallet) {
    BigDecimal points = BigDecimal.valueOf(pointsWallet.getPoints());
    BigDecimal price = trx.getInitialAmount();

    // init points trx
    PointsTrx pointsTrx = new PointsTrx();
    pointsTrx.setPointsWallet(pointsWallet);
    pointsTrx.setDescription("Points used to purchase tickets to " + trx.getEvent().getTitle());

    if (points.compareTo(price) < 0) {
      int pointsUsed = pointsWallet.getPoints(); // use all points available
      pointsWallet.setPoints(0);
      pointsTrx.setPoints(-pointsUsed);
    } else if (points.compareTo(price) > 0) {

      /* if points amount is more than the price, it can be used to cover all the cost
      resulting in free transaction */

      int endPoints = points.subtract(price).setScale(0, RoundingMode.HALF_UP).intValue();
      trx.setTotalAmount(BigDecimal.ZERO);
      pointsWallet.setPoints(endPoints);
      pointsTrx.setPoints(price.intValue());
    } else {
      throw new InsufficientPointsException("Insufficient points to be used !!");
    }
    pointsWalletService.savePointsWallet(pointsWallet);
    pointsTrxService.savePointsTrx(pointsTrx);
    return pointsTrx;
  }

  private BigDecimal useVoucher(TrxPurchaseRequestDto requestDto, Trx trx) {
    BigDecimal discount = BigDecimal.ZERO;
    if (!requestDto.getVoucherCode().isEmpty()) {
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
      discount = applyVoucher(trx, voucher);
    }
    return discount;
  }

  private BigDecimal applyVoucher(Trx trx, Voucher voucher) {
    BigDecimal discount;
    Users user = trx.getUser();
    Users awardee = voucher.getAwardee();
    if (awardee == null || awardee.equals(user)) {
      discount = trx.getInitialAmount()
          .multiply(BigDecimal.valueOf(voucher.getPercentDiscount()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
      voucher.setUseLimit(voucher.getUseLimit() - 1);
      voucherService.saveVoucher(voucher);
    } else {
      throw new NotAwardeeException("This voucher was not meant for you >:C");
    }
    return discount;
  }

  // set payment method
  private void setPaymentMethod(TrxPurchaseRequestDto requestDto, Trx trx) {
    Payment payment = paymentRepository.findById(requestDto.getPaymentId()).orElseThrow(() ->
        new PaymentMethodNotFoundException("Please enter a valid method of payment!"));
    trx.setPayment(payment);
  }

  // attendee set up
  private void setAttendee(Users user, Event event, Integer qty) {
    AttendeeId attendeeId = new AttendeeId(user.getId(), event.getId());

    // check if attendee exists
    Attendee attendee = attendeeService.findAttendee(attendeeId).orElse(new Attendee());
    attendee.setId(attendeeId);
    attendee.setUser(user);
    attendee.setEvent(event);
    attendee.setTicketsPurchased((attendee.getTicketsPurchased() == null ? 0 : attendee.getTicketsPurchased()) + qty);
    attendeeService.saveAttendee(attendee);
  }
}
