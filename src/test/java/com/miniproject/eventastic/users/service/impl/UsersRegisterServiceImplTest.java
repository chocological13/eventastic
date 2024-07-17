package com.miniproject.eventastic.users.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.miniproject.eventastic.exceptions.user.DuplicateCredentialsException;
import com.miniproject.eventastic.exceptions.user.UserNotFoundException;
import com.miniproject.eventastic.mail.service.MailService;
import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersRegisterService;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.service.VoucherService;
import java.awt.Point;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UsersRegisterServiceImplTest {


  @MockBean
  private UsersRepository usersRepository;
  @MockBean
  private PasswordEncoder passwordEncoder;
  @MockBean
  private PointsWalletService pointsWalletService;
  @MockBean
  private OrganizerWalletService organizerWalletService;
  @MockBean
  private UsersService usersService;
  @MockBean
  private PointsTrxService pointsTrxService;
  @MockBean
  private VoucherService voucherService;
  @MockBean
  private ReferralCodeUsageService referralCodeUsageService;
  @MockBean
  private MailService mailService;

  @InjectMocks
  private UsersRegisterService usersRegisterService = new UsersRegisterServiceImpl(usersRepository, passwordEncoder,
      pointsWalletService, organizerWalletService, usersService, pointsTrxService, voucherService,
      referralCodeUsageService, mailService);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    usersRegisterService = new UsersRegisterServiceImpl(usersRepository, passwordEncoder,
        pointsWalletService, organizerWalletService, usersService, pointsTrxService, voucherService,
        referralCodeUsageService, mailService);
  }

  @Test
  void testRegisterNewUser() {
    // Arrange
    RegisterRequestDto requestDto = new RegisterRequestDto();
    requestDto.setUsername("testuser");
    requestDto.setEmail("test@example.com");
    requestDto.setFullName("Test User");
    requestDto.setPassword("password");
    requestDto.setIsOrganizer(false);

    when(usersRepository.findByUsername("testuser")).thenReturn(Optional.empty());
    when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

    // Act
    RegisterResponseDto responseDto = usersRegisterService.register(requestDto);

    // Assert
    assertNotNull(responseDto);
    verify(usersRepository, times(2)).save(any(Users.class));
    verify(pointsWalletService).savePointsWallet(any(PointsWallet.class));
  }

  @Test
  void testRegisterUser_isOrganizer() {
    // Arrange
    RegisterRequestDto requestDto = new RegisterRequestDto();
    requestDto.setUsername("testuser");
    requestDto.setEmail("test@example.com");
    requestDto.setFullName("Test User");
    requestDto.setPassword("password");
    requestDto.setIsOrganizer(true);

    when(usersRepository.findByUsername("testuser")).thenReturn(Optional.empty());
    when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

    // Act
    RegisterResponseDto responseDto = usersRegisterService.register(requestDto);

    // Assert
    assertNotNull(responseDto);
    verify(usersRepository, times(2)).save(any(Users.class));
    verify(pointsWalletService).savePointsWallet(any(PointsWallet.class));
    verify(organizerWalletService).saveWallet(any(OrganizerWallet.class));
  }

  @Test
  void testRegisterUser_WithExistingUsername() {
    RegisterRequestDto requestDto = new RegisterRequestDto();
    requestDto.setUsername("testuser");

    when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(new Users()));

    // Act & Assert
    assertThrows(DuplicateCredentialsException.class, () -> usersRegisterService.register(requestDto));
  }

  @Test
  void testRegisterUser_WithExistingEmail() {
    // Arrange
    RegisterRequestDto requestDto = new RegisterRequestDto();
    requestDto.setUsername("testuser");
    requestDto.setEmail("test@example.com");

    when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Users()));

    // Act & Assert
    assertThrows(DuplicateCredentialsException.class, () -> usersRegisterService.register(requestDto));
  }
}
