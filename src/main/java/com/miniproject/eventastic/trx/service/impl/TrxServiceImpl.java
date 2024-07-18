package com.miniproject.eventastic.trx.service.impl;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.attendee.entity.AttendeeId;
import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.dashboard.dto.DailyStatisticsDto;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.ObjectNotFoundException;
import com.miniproject.eventastic.exceptions.event.EventEndedException;
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
import com.miniproject.eventastic.trx.metadata.Payment;
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
import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
@Slf4j
public class TrxServiceImpl implements TrxService {

  private final TrxRepository trxRepository;
  private final UsersService usersService;
  private final EventService eventService;
  private final TicketTypeService ticketTypeService;
  private final TicketService ticketService;
  private final PointsWalletService pointsWalletService;
  private final PointsTrxService pointsTrxService;
  private final VoucherService voucherService;
  private final PaymentRepository paymentRepository;
  private final AttendeeService attendeeService;
  private final OrganizerWalletTrxService organizerWalletTrxService;
  private final MailService mailService;

  @Override
  @Transactional
  public Trx purchaseTicket(TrxPurchaseRequestDto requestDto) throws UserNotFoundException, AccessDeniedException,
      TicketTypeNotFoundException, PointsWalletNotFoundException, InsufficientPointsException, VoucherNotFoundException, VoucherInvalidException, SeatUnavailableException, NotAwardeeException, PaymentMethodNotFoundException {
    // * get logged-in user
    Users loggedUser = usersService.getCurrentUser();
    Trx trx = new Trx();

    // * Get event
    Event eventPurchase = validateAndRetrieveEvent(requestDto.getEventId());
    log.info("Event: " + eventPurchase);

    // * Check TT and available seat
    TicketType ticketType = validateAndRetrieveTicketType(requestDto.getTicketTypeId());
    log.info("TicketType: " + ticketType.getName());

    // * Create Trx
    createTransaction(requestDto, loggedUser, eventPurchase, ticketType, trx);
    trxRepository.save(trx);
    System.out.println("Transaction created for user: " + loggedUser);

    // * points and voucher usage and payment method
    BigDecimal discount = calculateDiscount(requestDto, trx);
    BigDecimal promo = applyPromo(requestDto, trx);
    setPaymentMethod(requestDto, trx);

    // finalize trx total amount
    BigDecimal amountBeforePoints = applyDiscountAndPromo(trx, discount, promo);
    BigDecimal amountAfterPoints = usePoints(requestDto, trx, amountBeforePoints);
    trx.setTotalAmount(amountAfterPoints);

    // set attendee for this purchase
    setAttendee(loggedUser, eventPurchase, requestDto.getQty());

    // send payout to organizer
    organizerWalletTrxService.sendPayout(trx);
    trx.setTrxDate(Instant.now());
    trx.setIsPaid(true);
    trxRepository.save(trx);

    // * send email confirmation
    sendConfirmationEmail(trx);
    return trx;
  }

  public void sendConfirmationEmail(Trx trx) {
    MailTemplate temp = new MailTemplate();
    temp = temp.buildPurchaseTemp(trx);
    // ! TODO : uncomment in production, suspend email sending for local
    mailService.sendEmail(temp);
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

  @Override
  public Page<Trx> getTrxsByOrganizer(Users organizer, Pageable pageable) {
    Page<Trx> trxesPage= trxRepository.findTrxByEvent_Organizer(organizer, pageable);
    if (trxesPage.isEmpty()) {
      throw new ObjectNotFoundException("No one has bought tickets to your events yet :(");
    }
    return trxesPage;
  }

  @Override
  public List<DailyStatisticsDto> getDailyStatisticse(Users organizer, LocalDate startDate, LocalDate endDate) {
    ZoneOffset offset = ZoneOffset.UTC;  // or your specific offset
    Instant start = startDate.atStartOfDay().toInstant(offset);
    Instant end = endDate.plusDays(1).atStartOfDay().toInstant(offset).minusNanos(1);
    List<DailyStatisticsDto> dailyStatisticsDtos = trxRepository.getDailyStatistics(organizer, start, end);
    if (dailyStatisticsDtos.isEmpty()) {
      throw new ObjectNotFoundException("No daily statistics to show");
    }
    return dailyStatisticsDtos;
  }


  // Region - utilities for purchase ticket
  public Event validateAndRetrieveEvent(Long eventId) {
    Event event = eventService.getEventById(eventId);
    if (event.getEventDate().isBefore(LocalDate.now())) {
      throw new EventEndedException("Event has ended. Please choose another event.");
    }
    return event;
  }

  public TicketType validateAndRetrieveTicketType(Long ticketTypeId) throws TicketTypeNotFoundException,
      SeatUnavailableException {
    TicketType ticketType = ticketTypeService.getTicketTypeById(ticketTypeId);
    if (ticketType.getSeatAvailability() <= 0) {
      throw new SeatUnavailableException("Seat for this ticket type is sold out!");
    }
    return ticketType;
  }

  public void createTransaction(TrxPurchaseRequestDto requestDto, Users loggedUser, Event event,
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

  public BigDecimal calculateInitialAmount(TicketType ticketType, int qty) {
    BigDecimal unitPrice = ticketType.getPrice();
    return unitPrice.multiply(BigDecimal.valueOf(qty)).stripTrailingZeros().setScale(0, RoundingMode.HALF_UP);
  }

  public Set<Ticket> generateAndSaveTickets(Users loggedUser, TicketType ticketType, int qty) {
    Set<Ticket> ticketSet = new LinkedHashSet<>();
    for (int i = 0; i < qty; i++) {
      Ticket ticket = ticketService.generateTicket(loggedUser, ticketType);
      ticketService.saveTicket(ticket);
      ticketSet.add(ticket);
    }
    return ticketSet;
  }

  public void decrementAvailableSeat(Event event, TicketType ticketType, int qty) {
    event.setSeatAvailability(event.getSeatAvailability() - qty);
    eventService.saveEvent(event);

    ticketType.setSeatAvailability(ticketType.getSeatAvailability() - qty);
    ticketTypeService.saveTicketType(ticketType);
  }

  public BigDecimal usePoints(TrxPurchaseRequestDto requestDto, Trx trx, BigDecimal amountBeforePoints) throws PointsWalletNotFoundException {
    BigDecimal amountAfterPoints = BigDecimal.ZERO;
    if (requestDto.getUsingPoints()) {
      PointsWallet pointsWallet = pointsWalletService.getPointsWallet(trx.getUser());
      amountAfterPoints = applyPoints(trx, pointsWallet, amountBeforePoints);
      trx.setPointsWallet(pointsWallet);
    }
    return amountAfterPoints;
  }

  public BigDecimal applyPoints(Trx trx, PointsWallet pointsWallet, BigDecimal amountBeforePoints) throws InsufficientPointsException {
    BigDecimal amountAfterPoints = BigDecimal.ZERO;
    BigDecimal points = BigDecimal.valueOf(pointsWallet.getPoints());

    // init points trx
    PointsTrx pointsTrx = new PointsTrx();
    pointsTrx.setPointsWallet(pointsWallet);
    pointsTrx.setDescription("Points used to purchase tickets to " + trx.getEvent().getTitle());
    pointsTrx.setTrx(trx);

    if (points.compareTo(amountBeforePoints) < 0) {
      int pointsUsed = pointsWallet.getPoints(); // use all points available
      pointsWallet.setPoints(0);
      pointsTrx.setPoints(-pointsUsed);
      amountAfterPoints = amountBeforePoints.subtract(BigDecimal.valueOf(pointsUsed));
    } else if (points.compareTo(amountBeforePoints) > 0) {

      /* if points amount is more than the price, it can be used to cover all the cost
      resulting in free transaction */

      int endPoints = points.subtract(amountBeforePoints).setScale(0, RoundingMode.HALF_UP).intValue();
      amountAfterPoints = BigDecimal.ZERO;
      pointsWallet.setPoints(endPoints);
      pointsTrx.setPoints(-amountBeforePoints.intValue());
    } else {
      throw new InsufficientPointsException("Insufficient points to be used !!");
    }
    pointsWalletService.savePointsWallet(pointsWallet);
    pointsTrxService.savePointsTrx(pointsTrx);

    return amountAfterPoints;
  }

  public BigDecimal calculateDiscount(TrxPurchaseRequestDto requestDto, Trx trx) throws RuntimeException {
    BigDecimal discount = BigDecimal.ZERO;
    if (!requestDto.getVoucherCode().isEmpty()) {
      String voucherCode = requestDto.getVoucherCode();
      Voucher usedVoucher = voucherService.useVoucher(voucherCode, trx.getUser(), trx.getEvent());
      trx.setVoucher(usedVoucher);
      discount = applyVoucher(trx, usedVoucher);
    }
    return discount;
  }

  public BigDecimal applyVoucher(Trx trx, Voucher voucher) throws VoucherInvalidException {
    Event event = trx.getEvent();
    String voucherCode = voucher.getCode();
    // check if it's a referral voucher
    if (voucherCode.startsWith("REF10")) {
      int currentAvailability = event.getReferralVoucherUsageAvailability();
      if (currentAvailability <= 0) {
        throw new VoucherInvalidException("Referral voucher usage limit exceeded for this event");
      }
      event.setReferralVoucherUsageAvailability(currentAvailability - 1);
      eventService.saveEvent(event);
    }
    BigDecimal percent = BigDecimal.valueOf(voucher.getPercentDiscount())
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    return trx.getInitialAmount().multiply(percent);
  }

  public BigDecimal applyPromo(TrxPurchaseRequestDto requestDto, Trx trx) {
    Instant now = Instant.now();
    BigDecimal promo = BigDecimal.ZERO;
    if (trx.getEvent().getPromoPercent() != null && trx.getEvent().getPromoEndDate().isAfter(now)) {
      BigDecimal promoPercent = BigDecimal.valueOf(trx.getEvent().getPromoPercent()).divide(BigDecimal.valueOf(100), 2,
          RoundingMode.HALF_UP);
      promo = trx.getInitialAmount().multiply(promoPercent);
    }
    return promo;
  }

  // set payment method
  public void setPaymentMethod(TrxPurchaseRequestDto requestDto, Trx trx) throws PaymentMethodNotFoundException {
    Payment payment = paymentRepository.findById(requestDto.getPaymentId()).orElseThrow(() ->
        new PaymentMethodNotFoundException("Please enter a valid method of payment!"));
    trx.setPayment(payment);
  }

  public BigDecimal applyDiscountAndPromo(Trx trx, BigDecimal discount, BigDecimal promo) {
    BigDecimal amountBeforePoint = BigDecimal.ZERO;
    BigDecimal initAmount = trx.getInitialAmount();
    BigDecimal toBeDeducted = discount.add(promo);
    amountBeforePoint = initAmount.subtract(toBeDeducted);

    return amountBeforePoint;
  }

  // attendee set up
  public void setAttendee(Users user, Event event, Integer qty) {
    AttendeeId attendeeId = new AttendeeId(user.getId(), event.getId());

    // check if attendee exists
    Attendee attendee = attendeeService.findAttendee(attendeeId).orElse(new Attendee());
    attendee.setId(attendeeId);
    attendee.setUser(user);
    attendee.setEvent(event);
    attendee.setAttendingAt(event.getEventDate());
    attendee.setTicketsPurchased((attendee.getTicketsPurchased() == null ? 0 : attendee.getTicketsPurchased()) + qty);
    attendeeService.saveAttendee(attendee);
  }
}
