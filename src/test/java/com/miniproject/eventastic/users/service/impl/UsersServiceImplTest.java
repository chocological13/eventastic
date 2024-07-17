package com.miniproject.eventastic.users.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.miniproject.eventastic.attendee.service.AttendeeService;
import com.miniproject.eventastic.auth.entity.dto.changePassword.ChangePasswordRequestDto;
import com.miniproject.eventastic.image.service.CloudinaryService;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.profile.UserProfileDto;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.voucher.service.VoucherService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
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
  private ReferralCodeUsageService referralCodeUsageService;
  @Mock
  private PointsWalletService pointsWalletService;
  @Mock
  private CloudinaryService cloudinaryService;
  @Mock
  private ImageService imageService;
  @Mock
  private PointsTrxService pointsTrxService;
  @Mock
  private OrganizerWalletService organizerWalletService;
  @Mock
  private AttendeeService attendeeService;
  @Mock
  private VoucherService voucherService;
  @Mock
  private ModelMapper modelMapper;
  @InjectMocks
  private UsersServiceImpl usersService = new UsersServiceImpl(usersRepository, passwordEncoder,
      authenticationManager, referralCodeUsageService, pointsWalletService, cloudinaryService,
      imageService, pointsTrxService, organizerWalletService, attendeeService, voucherService, modelMapper);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    usersService = new UsersServiceImpl(usersRepository, passwordEncoder,
        authenticationManager, referralCodeUsageService, pointsWalletService, cloudinaryService,
        imageService, pointsTrxService, organizerWalletService, attendeeService, voucherService, modelMapper);
  }

  @Test
  void testGetCurrentUser() {
    // Arrange
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    when(authentication.getName()).thenReturn("testUser");
    Users mockUser = new Users();
    when(usersRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

    // Act
    Users result = usersService.getCurrentUser();

    // Assert
    assertNotNull(result);
    verify(usersRepository).findByUsername("testUser");
  }

  @Test
  void testGetProfile() {
    // Arrange
    Users mockUser = new Users();
    mockUser.setUsername("testUser");
    when(usersRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getName()).thenReturn("testUser");

    // Act
    UserProfileDto result = usersService.getProfile();

    // Assert
    assertNotNull(result);
    assertEquals("testUser", result.getUsername());
  }


  @Test
  void testChangePassword() {
    // Arrange
    Users mockUser = new Users();
    mockUser.setPassword(passwordEncoder.encode("oldPassword"));
    when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches("oldPassword", mockUser.getPassword())).thenReturn(true);

    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getName()).thenReturn("testUser");

    ChangePasswordRequestDto requestDto = new ChangePasswordRequestDto();
    requestDto.setOldPassword("oldPassword");
    requestDto.setNewPassword("newPassword");
    requestDto.setConfirmPassword("newPassword");

    // Act
    usersService.changePassword(requestDto);

    // Assert
    verify(usersRepository).save(mockUser);
    verify(passwordEncoder).encode("newPassword");
  }

  @Test
  void testUpdate() {
    // Arrange
    Users mockUser = new Users();
    when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getName()).thenReturn("testUser");

    ProfileUpdateRequestDTO requestDto = new ProfileUpdateRequestDTO();
    requestDto.setFullName("New Name");

    // Act
    usersService.update(requestDto);

    // Assert
    verify(usersRepository).save(mockUser);
    assertEquals("New Name", mockUser.getFullName());
  }

}
