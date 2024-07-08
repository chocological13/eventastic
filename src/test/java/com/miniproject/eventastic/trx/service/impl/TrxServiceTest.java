//package com.miniproject.eventastic.trx.service.impl;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.miniproject.eventastic.attendee.service.AttendeeService;
//import com.miniproject.eventastic.event.entity.Event;
//import com.miniproject.eventastic.event.repository.EventRepository;
//import com.miniproject.eventastic.event.service.EventService;
//import com.miniproject.eventastic.exceptions.event.EventEndedException;
//import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
//import com.miniproject.eventastic.exceptions.trx.InsufficientPointsException;
//import com.miniproject.eventastic.exceptions.trx.SeatUnavailableException;
//import com.miniproject.eventastic.exceptions.trx.TicketTypeNotFoundException;
//import com.miniproject.eventastic.exceptions.trx.VoucherInvalidException;
//import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
//import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
//import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
//import com.miniproject.eventastic.ticket.entity.Ticket;
//import com.miniproject.eventastic.ticket.service.TicketService;
//import com.miniproject.eventastic.ticketType.entity.TicketType;
//import com.miniproject.eventastic.ticketType.service.TicketTypeService;
//import com.miniproject.eventastic.trx.entity.Trx;
//import com.miniproject.eventastic.trx.entity.dto.TrxPurchaseRequestDto;
//import com.miniproject.eventastic.trx.metadata.Payment;
//import com.miniproject.eventastic.trx.repository.PaymentRepository;
//import com.miniproject.eventastic.trx.repository.TrxRepository;
//import com.miniproject.eventastic.users.entity.Users;
//import com.miniproject.eventastic.users.service.UsersService;
//import com.miniproject.eventastic.voucher.entity.Voucher;
//import com.miniproject.eventastic.voucher.service.VoucherService;
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.Optional;
//import java.util.Set;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//public class TrxServiceTest {
//
//  @Mock
//  private TrxRepository trxRepository;
//  @Mock
//  private PaymentRepository paymentRepository;
//  @Mock
//  private EventService eventService;
//  @Mock
//  private TicketTypeService ticketTypeService;
//  @Mock
//  private PointsWalletService pointsWalletService;
//  @Mock
//  private VoucherService voucherService;
//  @Mock
//  private UsersService usersService;
//  @Mock
//  private EventRepository eventRepository;
//  @Mock
//  private TicketService ticketService;
//  @Mock
//  private AttendeeService attendeeService;
//
//  @InjectMocks
//  private TrxServiceImpl trxService;
//
//  private Users mockUser;
//  private Event mockEvent;
//  private TicketType mockTicketType;
//  private TrxPurchaseRequestDto mockRequestDto;
//  private Payment mockPayment;
//
//  @BeforeEach
//  public void setUp() {
//    MockitoAnnotations.openMocks(this);
//    mockUser = new Users();
//    mockUser.setId(1L);
//
//    mockEvent = new Event();
//    mockEvent.setId(1L);
//    mockEvent.setEventDate(LocalDate.now().plusDays(1));
//    mockEvent.setAvailableSeat(100);
//
//    mockTicketType = new TicketType();
//    mockTicketType.setId(1L);
//    mockTicketType.setPrice(BigDecimal.valueOf(50));
//    mockTicketType.setAvailableSeat(50);
//
//    mockRequestDto = new TrxPurchaseRequestDto();
//    mockRequestDto.setEventId(1L);
//    mockRequestDto.setTicketTypeId(1L);
//    mockRequestDto.setQty(2);
//    mockRequestDto.setUsingPoints(false);
//    mockRequestDto.setVoucherCode(null);
//    mockRequestDto.setPaymentId(1L);
//
//    mockPayment = new Payment();
//    mockPayment.setId(1L);
//    mockPayment.setBankName("Mock Bank");
//    mockPayment.setAccountNumber("69420");
//
//    when(usersService.getCurrentUser()).thenReturn(mockUser);
//  }
//
//  @Test
//  public void testPurchaseTicket_success() {
//    when(usersService.getCurrentUser()).thenReturn(mockUser);
//    when(eventService.getEventById(1L)).thenReturn(mockEvent);
//    when(ticketTypeService.getTicketTypeById(1L)).thenReturn(mockTicketType);
//    when(trxRepository.save(any(Trx.class))).thenReturn(new Trx());
//    when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));
//
//    Trx trx = trxService.purchaseTicket(mockRequestDto);
//
//    assertEquals(mockUser, trx.getUser());
//    assertEquals(mockEvent, trx.getEvent());
//    assertEquals(mockTicketType, trx.getTicketType());
//    assertEquals(2, trx.getQty());
//    assertEquals(BigDecimal.valueOf(100), trx.getInitialAmount());
//    verify(trxRepository, times(1)).save(any(Trx.class));
//  }
//
//  @Test
//  public void testPurchaseTicket_eventNotFound() {
//    when(usersService.getCurrentUser()).thenReturn(mockUser);
//    when(eventService.getEventById(1L)).thenThrow(new EventNotFoundException("Event not found!"));
//
//    assertThrows(EventNotFoundException.class, () -> trxService.purchaseTicket(mockRequestDto));
//  }
//
//  @Test
//  public void testPurchaseTicket_eventEnded() {
//    mockEvent.setEventDate(LocalDate.now().minusDays(1));
//    when(usersService.getCurrentUser()).thenReturn(mockUser);
//    when(eventService.getEventById(1L)).thenReturn(mockEvent);
//
//    assertThrows(EventEndedException.class, () -> trxService.purchaseTicket(mockRequestDto));
//  }
//
//  @Test
//  public void testPurchaseTicket_ticketTypeNotFound() {
//    when(usersService.getCurrentUser()).thenReturn(mockUser);
//    when(eventService.getEventById(1L)).thenReturn(mockEvent);
//    when(ticketTypeService.getTicketTypeById(1L)).thenThrow(new TicketTypeNotFoundException("This ticket type does not exist!"));
//
//    assertThrows(TicketTypeNotFoundException.class, () -> trxService.purchaseTicket(mockRequestDto));
//  }
//
//  @Test
//  public void testPurchaseTicket_seatUnavailable() {
//    mockTicketType.setAvailableSeat(0);
//    when(usersService.getCurrentUser()).thenReturn(mockUser);
//    when(eventService.getEventById(1L)).thenReturn(mockEvent);
//    when(ticketTypeService.getTicketTypeById(1L)).thenReturn(mockTicketType);
//
//    assertThrows(SeatUnavailableException.class, () -> trxService.purchaseTicket(mockRequestDto));
//  }
//
//  @Test
//  public void testPurchaseTicket_insufficientPoints() {
//    mockRequestDto.setUsingPoints(true);
//    PointsWallet pointsWallet = new PointsWallet();
//    pointsWallet.setPoints(0);
//
//    when(usersService.getCurrentUser()).thenReturn(mockUser);
//    when(eventService.getEventById(1L)).thenReturn(mockEvent);
//    when(ticketTypeService.getTicketTypeById(1L)).thenReturn(mockTicketType);
//    when(pointsWalletService.getPointsWallet(mockUser)).thenReturn(pointsWallet);
//
//    assertThrows(InsufficientPointsException.class, () -> trxService.purchaseTicket(mockRequestDto));
//  }
//
//  @Test
//  public void testPurchaseTicket_voucherNotFound() {
//    mockRequestDto.setVoucherCode("INVALID_VOUCHER");
//
//    when(usersService.getCurrentUser()).thenReturn(mockUser);
//    when(eventService.getEventById(1L)).thenReturn(mockEvent);
//    when(ticketTypeService.getTicketTypeById(1L)).thenReturn(mockTicketType);
//    when(voucherService.getVoucher("INVALID_VOUCHER")).thenThrow(new VoucherNotFoundException("This voucher does not exist!"));
//
//    assertThrows(VoucherNotFoundException.class, () -> trxService.purchaseTicket(mockRequestDto));
//  }
//
//  @Test
//  public void testPurchaseTicket_voucherExpired() {
//    Voucher voucher = new Voucher();
//    voucher.setExpiresAt(Instant.now().minusSeconds(60));
//    mockRequestDto.setVoucherCode("EXPIRED_VOUCHER");
//
//    when(usersService.getCurrentUser()).thenReturn(mockUser);
//    when(eventService.getEventById(1L)).thenReturn(mockEvent);
//    when(ticketTypeService.getTicketTypeById(1L)).thenReturn(mockTicketType);
//    when(voucherService.getVoucher("EXPIRED_VOUCHER")).thenReturn(voucher);
//
//    assertThrows(VoucherInvalidException.class, () -> trxService.purchaseTicket(mockRequestDto));
//  }
//
//  @Test
//  public void testPurchaseTicket_voucherUsedUp() {
//    Voucher voucher = new Voucher();
//    voucher.setCode("VOUCHER");
//    voucher.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
//    voucher.setUseLimit(0);
//    mockRequestDto.setVoucherCode("USED_UP_VOUCHER");
//
//    when(usersService.getCurrentUser()).thenReturn(mockUser);
//    when(eventService.getEventById(1L)).thenReturn(mockEvent);
//    when(ticketTypeService.getTicketTypeById(1L)).thenReturn(mockTicketType);
//    when(voucherService.getVoucher("USED_UP_VOUCHER")).thenReturn(voucher);
//
//    assertThrows(VoucherInvalidException.class, () -> trxService.purchaseTicket(mockRequestDto));
//  }
//
//  @Test
//  public void testGetUserTickets() {
//    Set<Ticket> mockTickets = Set.of(new Ticket(), new Ticket());
//    when(ticketService.findTicketsByUser(mockUser)).thenReturn(mockTickets);
//
//    Set<Ticket> userTickets = trxService.getUserTickets();
//
//    assertEquals(mockTickets.size(), userTickets.size());
//    verify(ticketService, times(1)).findTicketsByUser(mockUser);
//  }
//}
