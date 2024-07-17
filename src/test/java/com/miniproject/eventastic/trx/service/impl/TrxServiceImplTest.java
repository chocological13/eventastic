package com.miniproject.eventastic.trx.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.dashboard.dto.DailyStatisticsDto;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.ObjectNotFoundException;
import com.miniproject.eventastic.exceptions.event.EventEndedException;
import com.miniproject.eventastic.exceptions.trx.TicketNotFoundException;
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
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.service.VoucherService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class TrxServiceImplTest {


  @Mock
  private TrxRepository trxRepository;
  @Mock
  private UsersService usersService;
  @Mock
  private EventService eventService;
  @Mock
  private TicketTypeService ticketTypeService;
  @Mock
  private TicketService ticketService;
  @Mock
  private PointsWalletService pointsWalletService;
  @Mock
  private PointsTrxService pointsTrxService;
  @Mock
  private VoucherService voucherService;
  @Mock
  private PaymentRepository paymentRepository;
  @Mock
  private AttendeeService attendeeService;
  @Mock
  private OrganizerWalletTrxService organizerWalletTrxService;
  @Mock
  private MailService mailService;

  @InjectMocks
  private TrxServiceImpl trxService = new TrxServiceImpl(trxRepository, usersService, eventService, ticketTypeService
      , ticketService, pointsWalletService, pointsTrxService, voucherService, paymentRepository, attendeeService,
      organizerWalletTrxService, mailService);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    trxService = new TrxServiceImpl(trxRepository, usersService, eventService, ticketTypeService
        , ticketService, pointsWalletService, pointsTrxService, voucherService, paymentRepository, attendeeService,
        organizerWalletTrxService, mailService);
  }

  @Test
  void testPurchaseTicket_Success() throws Exception {
    // Arrange
    TrxPurchaseRequestDto requestDto = new TrxPurchaseRequestDto();
    requestDto.setEventId(1L);
    requestDto.setTicketTypeId(1L);
    requestDto.setQty(2);
    requestDto.setUsingPoints(false);
    requestDto.setVoucherCode("");
    requestDto.setPaymentId(1L);

    Users mockUser = new Users();
    Event mockEvent = new Event();
    mockEvent.setEventDate(LocalDate.now().plusDays(1));
    mockEvent.setSeatAvailability(100);
    TicketType mockTicketType = new TicketType();
    mockTicketType.setSeatAvailability(10);
    mockTicketType.setPrice(new BigDecimal("100"));

    when(usersService.getCurrentUser()).thenReturn(mockUser);
    when(eventService.getEventById(anyLong())).thenReturn(mockEvent);
    when(ticketTypeService.getTicketTypeById(anyLong())).thenReturn(mockTicketType);
    when(ticketService.generateTicket(any(), any())).thenReturn(new Ticket());
    when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(new Payment()));

    // Act
    Trx result = trxService.purchaseTicket(requestDto);

    // Assert
    assertNotNull(result);
    assertEquals(mockUser.getId(), result.getUser().getId());
    assertEquals(mockEvent, result.getEvent());
    assertEquals(mockTicketType, result.getTicketType());
    assertEquals(2, result.getQty());
    assertEquals(new BigDecimal("200"), result.getInitialAmount());
    assertTrue(result.getIsPaid());

    verify(trxRepository, times(2)).save(any(Trx.class));
    verify(ticketService, times(2)).saveTicket(any(Ticket.class));
    verify(eventService).saveEvent(any(Event.class));
    verify(ticketTypeService).saveTicketType(any(TicketType.class));
    verify(organizerWalletTrxService).sendPayout(any(Trx.class));
    verify(mailService).sendEmail(any(MailTemplate.class));
  }

  @Test
  void testPurchaseTicket_EventEnded() {
    // Arrange
    TrxPurchaseRequestDto requestDto = new TrxPurchaseRequestDto();
    requestDto.setEventId(1L);

    Event pastEvent = new Event();
    pastEvent.setEventDate(LocalDate.now().minusDays(1));

    when(eventService.getEventById(anyLong())).thenReturn(pastEvent);

    // Act & Assert
    assertThrows(EventEndedException.class, () -> trxService.purchaseTicket(requestDto));
  }

  @Test
  void testPurchaseTicket_WithPoints() throws Exception {
    // Arrange
    TrxPurchaseRequestDto requestDto = new TrxPurchaseRequestDto();
    requestDto.setEventId(1L);
    requestDto.setTicketTypeId(1L);
    requestDto.setQty(1);
    requestDto.setUsingPoints(true);
    requestDto.setVoucherCode("");
    requestDto.setPaymentId(1L);

    Users mockUser = new Users();

    Event mockEvent = new Event();
    mockEvent.setEventDate(LocalDate.now().plusDays(1));
    mockEvent.setSeatAvailability(100);

    TicketType mockTicketType = new TicketType();
    mockTicketType.setSeatAvailability(10);
    mockTicketType.setPrice(new BigDecimal("100"));

    PointsWallet mockPointsWallet = new PointsWallet();
    mockPointsWallet.setPoints(150);

    when(usersService.getCurrentUser()).thenReturn(mockUser);
    when(eventService.getEventById(anyLong())).thenReturn(mockEvent);
    when(ticketTypeService.getTicketTypeById(anyLong())).thenReturn(mockTicketType);
    when(ticketService.generateTicket(any(), any())).thenReturn(new Ticket());
    when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(new Payment()));
    when(pointsWalletService.getPointsWallet(any(Users.class))).thenReturn(mockPointsWallet);

    // Act
    Trx result = trxService.purchaseTicket(requestDto);

    // Assert
    assertNotNull(result);
    assertEquals(BigDecimal.ZERO, result.getTotalAmount());
    assertEquals(50, mockPointsWallet.getPoints());
    verify(pointsWalletService).savePointsWallet(any(PointsWallet.class));
    verify(pointsTrxService).savePointsTrx(any(PointsTrx.class));
  }

  @Test
  void testGetUserTickets_Success() throws Exception {
    // Arrange
    Users mockUser = new Users();
    Set<Ticket> mockTickets = new HashSet<>();
    mockTickets.add(new Ticket());
    mockTickets.add(new Ticket());

    when(usersService.getCurrentUser()).thenReturn(mockUser);
    when(ticketService.findTicketsByUser(any(Users.class))).thenReturn(mockTickets);

    // Act
    Set<Ticket> result = trxService.getUserTickets();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
  }

  @Test
  void testGetUserTickets_NoTickets() {
    // Arrange
    Users mockUser = new Users();
    when(usersService.getCurrentUser()).thenReturn(mockUser);
    when(ticketService.findTicketsByUser(any(Users.class))).thenReturn(new HashSet<>());

    // Act & Assert
    assertThrows(TicketNotFoundException.class, () -> trxService.getUserTickets());
  }

  @Test
  void testGetTrxsByOrganizer() {
    // Arrange
    Users mockOrganizer = new Users();
    Page<Trx> mockTrxPage = mock(Page.class);
    Pageable mockPageable = mock(Pageable.class);

    when(trxRepository.findTrxByEvent_Organizer(eq(mockOrganizer), eq(mockPageable))).thenReturn(mockTrxPage);
    when(mockTrxPage.isEmpty()).thenReturn(false);

    // Act
    Page<Trx> result = trxService.getTrxsByOrganizer(mockOrganizer, mockPageable);

    // Assert
    assertNotNull(result);
    assertEquals(mockTrxPage, result);
  }

  @Test
  void testGetTrxsByOrganizer_NoTransactions() {
    // Arrange
    Users mockOrganizer = new Users();
    Page<Trx> emptyPage = Page.empty();
    Pageable mockPageable = mock(Pageable.class);

    when(trxRepository.findTrxByEvent_Organizer(eq(mockOrganizer), eq(mockPageable))).thenReturn(emptyPage);

    // Act & Assert
    assertThrows(ObjectNotFoundException.class, () -> trxService.getTrxsByOrganizer(mockOrganizer, mockPageable));
  }

  @Test
  void testGetDailyStatistics() {
    // Arrange
    Users mockOrganizer = new Users();
    LocalDate startDate = LocalDate.now().minusDays(7);
    LocalDate endDate = LocalDate.now();
    List<DailyStatisticsDto> mockStats = List.of(new DailyStatisticsDto(), new DailyStatisticsDto());

    when(trxRepository.getDailyStatistics(eq(mockOrganizer), any(Instant.class), any(Instant.class))).thenReturn(
        mockStats);

    // Act
    List<DailyStatisticsDto> result = trxService.getDailyStatisticse(mockOrganizer, startDate, endDate);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
  }

  @Test
  void testGetDailyStatistics_NoData() {
    // Arrange
    Users mockOrganizer = new Users();
    LocalDate startDate = LocalDate.now().minusDays(7);
    LocalDate endDate = LocalDate.now();

    when(trxRepository.getDailyStatistics(eq(mockOrganizer), any(Instant.class), any(Instant.class))).thenReturn(
        Collections.emptyList());

    // Act & Assert
    assertThrows(ObjectNotFoundException.class,
        () -> trxService.getDailyStatisticse(mockOrganizer, startDate, endDate));
  }

}