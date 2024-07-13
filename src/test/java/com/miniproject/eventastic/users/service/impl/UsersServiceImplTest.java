//package com.miniproject.eventastic.users.service.impl;
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.isA;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.miniproject.eventastic.image.service.CloudinaryService;
//import com.miniproject.eventastic.image.service.ImageService;
//import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
//import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
//import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
//import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
//import com.miniproject.eventastic.pointsWallet.service.impl.PointsWalletService;
//import com.miniproject.eventastic.referralCodeUsage.repository.ReferralCodeUsageRepository;
//import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
//import com.miniproject.eventastic.users.entity.Users;
//import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
//import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
//import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
//import com.miniproject.eventastic.users.event.UserRegistrationEvent;
//import com.miniproject.eventastic.users.repository.UsersRepository;
//import java.util.Optional;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@SpringBootTest
//public class UsersServiceImplTest {
//
//  @Mock
//  private UsersRepository usersRepository;
//  @Mock
//  private PasswordEncoder passwordEncoder;
//  @Mock
//  private AuthenticationManager authenticationManager;
//  @Mock
//  private ReferralCodeUsageService referralCodeUsageService;
//  @Mock
//  private ApplicationEventPublisher eventPublisher;
//  @Mock
//  private PointsWalletService pointsWalletService;
//  @Mock
//  private CloudinaryService cloudinaryService;
//  @Mock
//  private ImageService imageService;
//  @Mock
//  private PointsTrxService pointsTrxService;
//  @Mock
//  private OrganizerWalletService organizerWalletService;
//  @Mock
//  private SecurityContext securityContext;
//  @Mock
//  private Authentication authentication;
//
//  @InjectMocks
//  private UsersServiceImpl usersService = new UsersServiceImpl(usersRepository, passwordEncoder,
//      authenticationManager, referralCodeUsageService, eventPublisher, pointsWalletService, cloudinaryService,
//      imageService, pointsTrxService, organizerWalletService);
//
//  private Users user;
//  private Users organizer;
//
//  @BeforeEach
//  public void setUp() {
//    MockitoAnnotations.openMocks(this);
//    usersService = new UsersServiceImpl(usersRepository, passwordEncoder,
//        authenticationManager, referralCodeUsageService, eventPublisher, pointsWalletService, cloudinaryService,
//        imageService, pointsTrxService, organizerWalletService);
//
////    // user role
////    user = new Users();
////    user.setId(1L);
////    user.setUsername("user");
////    user.setPassword("password");
////    user.setEmail("user@email.com");
////    user.setIsOrganizer(false);
////
////    // organizer
////    organizer = new Users();
////    organizer.setId(2L);
////    organizer.setUsername("organizer");
////    organizer.setPassword("password");
////    organizer.setIsOrganizer(true);
//  }
//
//  // Region
//  @Test
//  public void testGetCurrentUser_success() {
//    user = new Users();
//    user.setId(1L);
//    user.setUsername("user");
//    user.setPassword("password");
//    user.setEmail("user@email.com");
//    user.setIsOrganizer(false);
//
//    SecurityContextHolder.setContext(securityContext);
//    when(securityContext.getAuthentication()).thenReturn(authentication);
//    when(authentication.getName()).thenReturn(user.getUsername());
//    when(usersRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
//
//    Users user = usersService.getCurrentUser();
//
//    assertNotNull(user);
//    assertEquals("user", user.getUsername());
//  }
//
//  @Test
//  public void testGetCurrentUser_noAuthentication() {
//    SecurityContextHolder.setContext(securityContext);
//    when(securityContext.getAuthentication()).thenReturn(null);
//
//    assertThrows(AccessDeniedException.class, () -> usersService.getCurrentUser());
//  }
//
//  // * test getProfile
//  @Test
//  public void testGetProfile_success() {
//    // given
//    when(securityContext.getAuthentication()).thenReturn(authentication);
//    when(authentication.getName()).thenReturn(user.getUsername());
//    SecurityContextHolder.setContext(securityContext);
//
//    when(usersRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
//
//    // When
//    UserProfileDto profile = usersService.getProfile();
//    UserProfileDto compare = new UserProfileDto(usersService.getCurrentUser());
//
//    // then
//    assertNotNull(profile);
//    assertEquals("user", profile.getUsername());
//    assertEquals(profile, compare);
//  }
//
//  // REGISTER
//  @Test
////  public void testRegisterUserNotOrganizer_success() {
////    // given
////    RegisterRequestDto requestDto = new RegisterRequestDto();
////    requestDto.setUsername("testuser");
////    requestDto.setPassword("password");
////    requestDto.setEmail("email@email.com");
////    requestDto.setIsOrganizer(false);
////
////
////
////    Users newUser = new Users();
////    newUser.setId(1L);
////    newUser.setUsername("testuser");
////    newUser.setPassword("encodedPassword");
////    newUser.setEmail("email@email.com");
////    newUser.setIsOrganizer(false);
////    newUser.setOwnedRefCode("refcode");
////
////    PointsWallet newWallet = new PointsWallet();
////    newWallet.setId(1L);
////    newWallet.setUser(newUser);
////
////    newUser.setPointsWallet(newWallet);
////
////
////    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//////    when(newUser.getPointsWallet().getId()).thenReturn(newWallet.getId());
////    when(usersRepository.save(any(Users.class))).thenReturn(newUser);
////
////
////    // when
////    RegisterResponseDto response = usersService.register(requestDto);
////
////    // then
////    assertNotNull(response);
////    assertEquals("testuser", response.getUsername());
////    verify(passwordEncoder, times(1)).encode(anyString());
////    verify(usersRepository, times(1)).save(any(Users.class));
////    verify(eventPublisher, times(1)).publishEvent(any(UserRegistrationEvent.class));
////    verify(pointsWalletService, times(1)).savePointsWallet(any(PointsWallet.class));
////  }
//
//  @AfterEach
//  public void tearDown() {
//    SecurityContextHolder.clearContext();
//  }
//
////  // Region - Tests for showing points wallet
////  @Test
////  public void testGetPointsWallet_success() {
////    // points wallet
////    PointsWallet pointsWallet = new PointsWallet();
////    pointsWallet.setId(1L);
////    pointsWallet.setPoints(10000);
////    pointsWallet.setUser(user);
////
////    // Set up SecurityContext and Authentication
////    when(securityContext.getAuthentication()).thenReturn(authentication);
////    when(authentication.getName()).thenReturn("user");
////    SecurityContextHolder.setContext(securityContext);
////
////    // Mock repository and service methods
////    when(usersRepository.findByUsername("user")).thenReturn(Optional.of(user));
////    when(pointsWalletService.getPointsWallet(user)).thenReturn(new PointsWalletResponseDto(pointsWallet));
////    when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
////
////    PointsWalletResponseDto expectedResponse = new PointsWalletResponseDto(pointsWallet);
////
////    PointsWalletResponseDto actualResponse = usersService.getUsersPointsWallet();
////
////    assertEquals(expectedResponse, actualResponse);
////
////    verify(usersRepository, times(1)).findByUsername("user");
////    verify(pointsWalletService, times(1)).getPointsWallet(user);
////  }
//
//}
