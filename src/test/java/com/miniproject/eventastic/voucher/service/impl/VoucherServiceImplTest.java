//package com.miniproject.eventastic.voucher.service.impl;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.miniproject.eventastic.event.entity.Event;
//import com.miniproject.eventastic.event.repository.EventRepository;
//import com.miniproject.eventastic.event.service.impl.EventServiceImpl;
//import com.miniproject.eventastic.users.entity.Users;
//import com.miniproject.eventastic.users.repository.UsersRepository;
//import com.miniproject.eventastic.users.service.impl.UsersServiceImpl;
//import com.miniproject.eventastic.voucher.entity.Voucher;
//import com.miniproject.eventastic.voucher.entity.dto.create.CreateEventVoucherRequestDto;
//import com.miniproject.eventastic.voucher.repository.VoucherRepository;
//import java.nio.file.AccessDeniedException;
//import java.util.Optional;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//@SpringBootTest
//public class VoucherServiceImplTest {
//
//  @Mock
//  private VoucherRepository voucherRepository;
//  @Mock
//  private UsersServiceImpl usersService;
//  @Mock
//  private EventServiceImpl eventService;
//  @Mock
//  private UsersRepository usersRepository;
//  @Mock
//  private EventRepository eventRepository;
//  @Mock
//  private SecurityContext securityContext;
//
//  @InjectMocks
//  private VoucherServiceImpl voucherService = new VoucherServiceImpl(voucherRepository, usersService, eventService);
//
//  private Users user;
//  private Users organizer;
//  private Event event;
//
//  public Authentication organizerAuth;
//  public Authentication userAuth;
//
//  @BeforeEach
//  public void setUp() {
//    MockitoAnnotations.openMocks(this);
//    voucherService = new VoucherServiceImpl(voucherRepository, usersService, eventService);
//    // user role
//    user = new Users();
//    user.setId(1L);
//    user.setUsername("user");
//    user.setPassword("password");
//    usersRepository.save(user);
//    userAuth = new UsernamePasswordAuthenticationToken(user, "password");
//    // organizer
//    organizer = new Users();
//    organizer.setId(2L);
//    organizer.setUsername("organizer");
//    organizer.setPassword("password");
//    organizer.setIsOrganizer(true);
//    usersRepository.save(organizer);
//    organizerAuth = new UsernamePasswordAuthenticationToken(organizer, "password");
//    // mock event
//    event = new Event();
//    event.setId(1L);
//    event.setTitle("Test Event");
//    eventRepository.save(event);
//
//    // Mock repository methods
//    when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
//    when(usersRepository.findById(2L)).thenReturn(Optional.of(organizer));
//    when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
//
//    // Set up SecurityContext
//    when(securityContext.getAuthentication()).thenReturn(organizerAuth);
//    SecurityContextHolder.setContext(securityContext);
//  }
//
//  @Test
//  public void testCreateVoucherByOrganizer() throws AccessDeniedException {
//    // set up dto
//    CreateEventVoucherRequestDto requestDto = new CreateEventVoucherRequestDto();
//    requestDto.setCode("TEST");
//    requestDto.setDescription("test desc");
//    requestDto.setPercentDiscount(10);
//    requestDto.setValidity(1);
//    requestDto.setAwardeeId(1L);
//    requestDto.setEventId(1L);
//
//    when(usersService.getCurrentUser()).thenReturn(organizer);
//    when(usersService.getById(1L)).thenReturn(user);
//    when(eventService.getEventById(1L)).thenReturn(event);
//
//    Voucher createdVoucher = voucherService.createVoucher(requestDto);
//
//    assertNotNull(createdVoucher);
//    assertEquals("TEST", createdVoucher.getCode());
//    assertEquals("test desc", createdVoucher.getDescription());
//    assertEquals(10, createdVoucher.getPercentDiscount());
//    assertEquals(user, createdVoucher.getAwardee());
//    assertEquals(event, createdVoucher.getEvent());
//    verify(voucherRepository, times(1)).save(any(Voucher.class));
//  }
//
//
//  @AfterEach
//  public void tearDown() {
//    SecurityContextHolder.clearContext();
//  }
//
//
//
//}
