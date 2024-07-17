package com.miniproject.eventastic.voucher.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.exceptions.trx.VoucherInvalidException;
import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
import com.miniproject.eventastic.exceptions.user.DuplicateCredentialsException;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.VoucherUsage;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateEventVoucherRequestDto;
import com.miniproject.eventastic.voucher.repository.VoucherRepository;
import com.miniproject.eventastic.voucher.repository.VoucherUsageRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

@SpringBootTest
public class VocherServiceImplTest {

  @Mock
  private VoucherRepository voucherRepository;

  @Mock
  private VoucherUsageRepository voucherUsageRepository;

  @InjectMocks
  private VoucherServiceImpl voucherService = new VoucherServiceImpl(voucherRepository, voucherUsageRepository);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    voucherService = new VoucherServiceImpl(voucherRepository, voucherUsageRepository);
  }

  @Test
  void testUseVoucher_Success() {
    // Arrange
    String voucherCode = "TEST123";
    Users user = new Users();
    user.setId(1L);
    Event event = new Event();
    event.setId(1L);

    Voucher voucher = new Voucher();
    voucher.setId(1L);
    voucher.setCode(voucherCode);
    voucher.setIsActive(true);
    voucher.setExpiresAt(Instant.now().plusSeconds(3600));
    voucher.setUseAvailability(1);

    when(voucherRepository.findByCodeAndIsActiveTrue(voucherCode)).thenReturn(voucher);
    when(voucherUsageRepository.findById(any())).thenReturn(Optional.empty());

    // Act
    Voucher result = voucherService.useVoucher(voucherCode, user, event);

    // Assert
    assertNotNull(result);
    assertEquals(0, voucher.getUseAvailability());
    verify(voucherRepository, times(1)).save(voucher);
    verify(voucherUsageRepository, times(1)).save(any(VoucherUsage.class));
  }

  @Test
  void testUseVoucher_VoucherNotFound() {
    // Arrange
    String voucherCode = "INVALID";
    Users user = new Users();
    Event event = new Event();

    when(voucherRepository.findByCodeAndIsActiveTrue(voucherCode)).thenReturn(null);

    // Act & Assert
    assertThrows(VoucherNotFoundException.class, () -> voucherService.useVoucher(voucherCode, user, event));
  }

  @Test
  void testUseVoucher_ExpiredVoucher() {
    // Arrange
    String voucherCode = "EXPIRED";
    Users user = new Users();
    Event event = new Event();

    Voucher voucher = new Voucher();
    voucher.setCode(voucherCode);
    voucher.setIsActive(true);
    voucher.setExpiresAt(Instant.now().minusSeconds(3600));

    when(voucherRepository.findByCodeAndIsActiveTrue(voucherCode)).thenReturn(voucher);

    // Act & Assert
    assertThrows(VoucherInvalidException.class, () -> voucherService.useVoucher(voucherCode, user, event));
    assertFalse(voucher.getIsActive());
  }

  @Test
  void testCreateEventVoucher_Success() {
    // Arrange
    Users organizer = new Users();
    organizer.setIsOrganizer(true);
    Event event = new Event();
    CreateEventVoucherRequestDto requestDto = new CreateEventVoucherRequestDto();
    requestDto.setCode("NEWEVENT");
    requestDto.setValidity(7);
    requestDto.setUseLimit(50);

    when(voucherRepository.findByCodeAndIsActiveTrue(anyString())).thenReturn(null);

    // Act
    Voucher result = voucherService.createEventVoucher(organizer, event, requestDto);

    // Assert
    assertNotNull(result);
    assertEquals("NEWEVENT", result.getCode());
    assertEquals(50, result.getUseLimit());
    assertTrue(result.getIsActive());
    verify(voucherRepository, times(1)).save(any(Voucher.class));
  }

  @Test
  void testCreateEventVoucher_DuplicateCode() {
    // Arrange
    Users organizer = new Users();
    organizer.setIsOrganizer(true);
    Event event = new Event();
    CreateEventVoucherRequestDto requestDto = new CreateEventVoucherRequestDto();
    requestDto.setCode("DUPLICATE");

    when(voucherRepository.findByCodeAndIsActiveTrue(anyString())).thenReturn(new Voucher());

    // Act & Assert
    assertThrows(
        DuplicateCredentialsException.class, () -> voucherService.createEventVoucher(organizer, event, requestDto));
  }

  @Test
  void testCreateEventVoucher_UnauthorizedUser() {
    // Arrange
    Users nonOrganizer = new Users();
    nonOrganizer.setIsOrganizer(false);
    nonOrganizer.setUsername("regularuser");
    Event event = new Event();
    CreateEventVoucherRequestDto requestDto = new CreateEventVoucherRequestDto();

    // Act & Assert
    assertThrows(AccessDeniedException.class, () -> voucherService.createEventVoucher(nonOrganizer, event, requestDto));
  }

  @Test
  void testGetAwardeesVouchers_Success() {
    // Arrange
    Users user = new Users();
    user.setId(1L);
    List<Voucher> mockVouchers = Arrays.asList(new Voucher(), new Voucher());

    when(voucherRepository.findByAwardeeIdAndIsActiveTrue(1L)).thenReturn(mockVouchers);

    // Act
    List<Voucher> result = voucherService.getAwardeesVouchers(user);

    // Assert
    assertEquals(2, result.size());
  }

  @Test
  void testGetAwardeesVouchers_NoVouchers() {
    // Arrange
    Users user = new Users();
    user.setId(1L);

    when(voucherRepository.findByAwardeeIdAndIsActiveTrue(1L)).thenReturn(Arrays.asList());

    // Act & Assert
    assertThrows(VoucherNotFoundException.class, () -> voucherService.getAwardeesVouchers(user));
  }

  @Test
  void testGetEventVouchers_Success() {
    // Arrange
    Long eventId = 1L;
    List<Voucher> mockVouchers = Arrays.asList(new Voucher(), new Voucher());

    when(voucherRepository.findByEventIdAndIsActiveTrue(eventId)).thenReturn(mockVouchers);

    // Act
    List<Voucher> result = voucherService.getEventVouchers(eventId);

    // Assert
    assertEquals(2, result.size());
  }

  @Test
  void testGetEventVouchers_NoVouchers() {
    // Arrange
    Long eventId = 1L;

    when(voucherRepository.findByEventIdAndIsActiveTrue(eventId)).thenReturn(Arrays.asList());

    // Act & Assert
    assertThrows(VoucherNotFoundException.class, () -> voucherService.getEventVouchers(eventId));
  }

  @Test
  void testGetVouchersForAllUsers_Success() {
    // Arrange
    List<Voucher> mockVouchers = Arrays.asList(new Voucher(), new Voucher());

    when(voucherRepository.findByAwardeeIdIsNullAndExpiresAtIsAfter(any())).thenReturn(mockVouchers);

    // Act
    List<Voucher> result = voucherService.getVouchersForAllUsers();

    // Assert
    assertEquals(2, result.size());
  }

  @Test
  void testGetVouchersForAllUsers_NoVouchers() {
    // Arrange
    when(voucherRepository.findByAwardeeIdIsNullAndExpiresAtIsAfter(any())).thenReturn(Arrays.asList());

    // Act & Assert
    assertThrows(VoucherNotFoundException.class, () -> voucherService.getVouchersForAllUsers());
  }
}
