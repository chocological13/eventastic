package com.miniproject.eventastic.users.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersRegisterService;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.service.VoucherService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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

  @InjectMocks
  private UsersRegisterService usersRegisterService = new UsersRegisterServiceImpl(usersRepository, passwordEncoder,
      pointsWalletService, organizerWalletService, usersService, pointsTrxService, voucherService,
      referralCodeUsageService);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    usersRegisterService = new UsersRegisterServiceImpl(usersRepository, passwordEncoder,
        pointsWalletService, organizerWalletService, usersService, pointsTrxService, voucherService,
        referralCodeUsageService);
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
}
