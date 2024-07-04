package com.miniproject.eventastic.users.service.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
import com.miniproject.eventastic.pointsWallet.service.impl.PointsWalletService;
import com.miniproject.eventastic.referralCodeUsage.repository.ReferralCodeUsageRepository;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.repository.UsersRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UsersServiceImplTest {

  @Mock
  private UsersRepository usersRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private ReferralCodeUsageRepository referralCodeUsageRepository;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private PointsWalletService pointsWalletService;
  @Mock
  private SecurityContext securityContext;
  @Mock
  private Authentication authentication;

  @InjectMocks
  private UsersServiceImpl usersService = new UsersServiceImpl(usersRepository, passwordEncoder,
      authenticationManager, referralCodeUsageRepository, eventPublisher, pointsWalletService);

  private Users user;
  private Users organizer;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    usersService = new UsersServiceImpl(usersRepository, passwordEncoder, authenticationManager, referralCodeUsageRepository, eventPublisher, pointsWalletService);
    // user role
    user = new Users();
    user.setId(1L);
    user.setUsername("user");
    user.setPassword("password");
    // organizer
    organizer = new Users();
    organizer.setId(2L);
    organizer.setUsername("organizer");
    organizer.setPassword("password");
    organizer.setIsOrganizer(true);
  }


  // Region - Tests for showing points wallet
  @Test
  public void testGetPointsWallet_success() {
    // points wallet
    PointsWallet pointsWallet = new PointsWallet();
    pointsWallet.setId(1L);
    pointsWallet.setPoints(10000);
    pointsWallet.setUser(user);

    // Set up SecurityContext and Authentication
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("user");
    SecurityContextHolder.setContext(securityContext);

    // Mock repository and service methods
    when(usersRepository.findByUsername("user")).thenReturn(Optional.of(user));
    when(pointsWalletService.getPointsWallet(user)).thenReturn(new PointsWalletResponseDto(pointsWallet));
    when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

    PointsWalletResponseDto expectedResponse = new PointsWalletResponseDto(pointsWallet);

    PointsWalletResponseDto actualResponse = usersService.getUsersPointsWallet();

    assertEquals(expectedResponse, actualResponse);

    verify(usersRepository, times(1)).findByUsername("user");
    verify(pointsWalletService, times(1)).getPointsWallet(user);
  }

}
