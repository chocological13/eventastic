//package com.miniproject.eventastic.users.event.listener;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//import com.miniproject.eventastic.exceptions.user.UserNotFoundException;
//import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
//import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
//import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
//import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
//import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
//import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
//import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
//import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
//import com.miniproject.eventastic.users.entity.Users;
//import com.miniproject.eventastic.users.event.UserRegistrationEvent;
//import com.miniproject.eventastic.users.service.UsersService;
//import com.miniproject.eventastic.voucher.entity.Voucher;
//import com.miniproject.eventastic.voucher.service.VoucherService;
//import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//@SpringBootTest
//@Slf4j
//@Transactional
//class UserRegistrationListenerTest {
//
//  @MockBean
//  private PointsWalletService pointsWalletService;
//  @MockBean
//  private UsersService usersService;
//  @MockBean
//  private ReferralCodeUsageService referralCodeUsageService;
//  @MockBean
//  private PointsTrxService pointsTrxService;
//  @MockBean
//  private OrganizerWalletService organizerWalletService;
//  @MockBean
//  private VoucherService voucherService;
//  @MockBean
//  private UserRegistrationEvent event;
//
//  @InjectMocks
//  public UserRegistrationListener listener;
//
//  private AutoCloseable closable;
//  private Users user;
//
//
//  @BeforeEach
//  public void setUp() {
//    closable = MockitoAnnotations.openMocks(this);
//
//    user = new Users();
//    user.setId(1L);
//    user.setUsername("username");
//    user.setPassword("password");
//    user.setEmail("email@email.com");
//
//    event = new UserRegistrationEvent(this, user);
//    listener = new UserRegistrationListener(pointsWalletService, usersService,
//        referralCodeUsageService, pointsTrxService, organizerWalletService, voucherService);
//  }
//
//  @AfterEach
//  void tearDown() throws Exception {
//    closable.close();
//  }
//
//  @Test
//  public void testHandleRegistrationEvent_withReferralCode() {
//    // given
//    PointsWallet pointsWallet = new PointsWallet();
//    pointsWallet.setId(2L);
//    pointsWallet.setPoints(0);
//
//    Users codeOwner = new Users();
//    codeOwner.setId(2L);
//    codeOwner.setOwnedRefCode("REF123");
//    codeOwner.setPointsWallet(pointsWallet);
//
//    pointsWallet.setUser(codeOwner);
//
//    user.setRefCodeUsed("REF123");
//
//    when(pointsWalletService.getPointsWallet(codeOwner)).thenReturn(pointsWallet);
//    when(usersService.getUserByOwnedCode("REF123")).thenReturn(codeOwner);
//
//    // when
//    listener.handleUserRegistrationEvent(this.event);
//
//    // then
//    assertEquals(user.getRefCodeUsed(), codeOwner.getOwnedRefCode());
//    assertEquals(codeOwner.getPointsWallet().getPoints(), 10000);
//    assertNotNull(pointsWallet.getPointsTrxes());
//    verify(pointsTrxService).savePointsTrx(any(PointsTrx.class));
//    verify(pointsWalletService, times(2)).savePointsWallet(any(PointsWallet.class));
//    verify(voucherService, times(1)).saveVoucher(any(Voucher.class));
//    verify(referralCodeUsageService, times(1)).saveReferralCodeUsage(any(ReferralCodeUsage.class));
//  }
//
//  @Test
//  public void testHandleUserRegistrationEvent_WithoutReferralCode() {
//    listener.handleUserRegistrationEvent(this.event);
//
//    verify(pointsWalletService).savePointsWallet(any(PointsWallet.class));
//    verify(usersService).saveUser(user);
//    assertNotNull(user.getOwnedRefCode());
//    assertNull(user.getRefCodeUsed());
//  }
//
//  @Test
//  public void testHandleUserRegistrationEvent_TransactionalRollback() {
//    // arrange
//    doThrow(new RuntimeException("Simulated exception")).when(usersService).saveUser(user);
//
//    // act
//    assertThrows(RuntimeException.class, () -> listener.handleUserRegistrationEvent(this.event));
//
//    // Assert
//    verify(pointsWalletService, times(1)).savePointsWallet(any(PointsWallet.class));
//    verify(usersService, times(1)).saveUser(user);
//    // methods that happens after usersService.saveUser
//    verify(organizerWalletService, never()).saveWallet(any(OrganizerWallet.class));
//    verify(referralCodeUsageService, never()).saveReferralCodeUsage(any(ReferralCodeUsage.class));
//    verify(pointsTrxService, never()).savePointsTrx(any(PointsTrx.class));
//    verify(voucherService, never()).saveVoucher(any(Voucher.class));
//  }
//
//  @Test
//  public void testHandleUserRegistrationEvent_IsOrganizer() {
//    // arrange
//    user.setIsOrganizer(true);
//
//    // act
//    listener.handleUserRegistrationEvent(this.event);
//
//    // assert
//    assertNotNull(user.getPointsWallet());
//    assertNotNull(user.getOrganizerWallet());
//    verify(organizerWalletService, times(1)).saveWallet(any(OrganizerWallet.class));
//    verify(pointsWalletService, times(1)).savePointsWallet(any(PointsWallet.class));
//    verify(usersService, times(1)).saveUser(user);
//  }
//
//  @Test
//  void testHandleUserRegistrationEvent_NullUser() {
//    // Arrange
//    event = mock(UserRegistrationEvent.class);
//    when(event.getUser()).thenReturn(null);
//
//    // Act & Assert
//    assertThrows(IllegalArgumentException.class, () -> listener.handleUserRegistrationEvent(event));
//    verify(pointsWalletService, never()).savePointsWallet(any());
//    verify(usersService, never()).saveUser(any());
//  }
//
//  @Test
//  public void testInitPointsWallet_success() {
//    listener.handleUserRegistrationEvent(this.event);
//
//    verify(pointsWalletService).savePointsWallet(any(PointsWallet.class));
//    assertNotNull(user.getPointsWallet());
//  }
//
//  @Test
//  public void testInitOrganizerWallet_success() {
//    // given
//    user.setIsOrganizer(true);
//
//    listener.handleUserRegistrationEvent(this.event);
//
//    // then
//    verify(organizerWalletService).saveWallet(any(OrganizerWallet.class));
//    assertNotNull(user.getOrganizerWallet());
//    log.info("Organizer wallet saved");
//  }
//
//  @Test
//  public void testGenerateAndAssignReferralCode_success() {
//    listener.handleUserRegistrationEvent(this.event);
//
//    assertNotNull(user.getOwnedRefCode());
//  }
//
//
//  @Test
//  public void testHandleUserRegistrationEvent_withEmptyRefCode() {
//    // given
//    user.setRefCodeUsed("");
//
//    // when
//    listener.handleUserRegistrationEvent(event);
//
//    // then
//    verify(usersService, times(1)).getUserByOwnedCode(anyString());
//    verify(pointsWalletService, times(1)).savePointsWallet(any(PointsWallet.class));
//    verify(pointsTrxService, never()).savePointsTrx(any(PointsTrx.class));
//    verify(voucherService, never()).saveVoucher(any(Voucher.class));
//    verify(referralCodeUsageService, never()).saveReferralCodeUsage(any(ReferralCodeUsage.class));
//  }
//
//  @Test
//  public void testUseRefCode_withInvalidRefCode() {
//    // arrange
//    when(usersService.getUserByOwnedCode(anyString())).thenReturn(null);
//
//    // act & assert
//    assertThrows(UserNotFoundException.class, () -> listener.useRefCode(user, anyString()));
//    verify(pointsWalletService, never()).savePointsWallet(any());
//    verify(pointsTrxService, never()).savePointsTrx(any());
//    verify(voucherService, never()).saveVoucher(any());
//    verify(referralCodeUsageService, never()).saveReferralCodeUsage(any());
//  }
//
//  @Test
//  public void testUseRefCode_generatedVoucherAlreadyExists() {
//    // arrange
//    when(voucherService.getVoucher(anyString()))
//        .thenReturn(new Voucher())  // First call, code exists
//        .thenReturn(null);    // Second call, code does not exist
//
//    // act
//    String code = listener.generateVoucher();
//
//    // assert
//    assertNotNull(code);
//    assertTrue(code.startsWith("REF10"));
//    verify(voucherService, times(2)).getVoucher(anyString());
//  }
//
//  @Test
//  public void testGenerateRefCode_refCodeExists() {
//    // arrange
//    when(usersService.getUserByOwnedCode(anyString()))
//        .thenReturn(new Users()) // first call returns a user, which means ref code exists
//        .thenReturn(null);    // second call returns null, which means ref code is good to go
//
//    // act
//    String code = listener.generateReferralCode();
//
//    // assert
//    verify(usersService, atLeast(2)).getUserByOwnedCode(anyString());
//    assertNotNull(code);
//  }
//
//  @Test
//  public void testUseRefCode_VoucherCreationFailure() {
//    // given
//    user.setRefCodeUsed("REF123");
//
//    PointsWallet ownerWallet = new PointsWallet();
//    ownerWallet.setId(2L);
//    ownerWallet.setPoints(0);
//
//    Users codeOwner = new Users();
//    codeOwner.setId(2L);
//    codeOwner.setUsername("codeOwner");
//    codeOwner.setOwnedRefCode("REF123");
//    codeOwner.setPointsWallet(ownerWallet);
//    ownerWallet.setUser(codeOwner);
//
//    when(usersService.getUserByOwnedCode("REF123")).thenReturn(codeOwner);
//    when(pointsWalletService.getPointsWallet(codeOwner)).thenReturn(ownerWallet);
//    doThrow(new RuntimeException("Voucher creation failed")).when(voucherService).saveVoucher(any(Voucher.class));
//
//    // when
//    assertThrows(RuntimeException.class, () -> listener.useRefCode(user, user.getRefCodeUsed()));
//
//    // then
//    verify(voucherService, times(1)).saveVoucher(any(Voucher.class));
//    // verify that no other actions are performed if this happens
//    verify(referralCodeUsageService, never()).saveReferralCodeUsage(any(ReferralCodeUsage.class));
//  }
//
//  @Test
//  void testUseRefCode_PointsTrxServiceException() {
//    // Arrange
//    user.setRefCodeUsed("REF123");
//
//    PointsWallet ownerWallet = new PointsWallet();
//    ownerWallet.setId(2L);
//    ownerWallet.setPoints(0);
//
//    Users codeOwner = new Users();
//    codeOwner.setId(2L);
//    codeOwner.setUsername("codeOwner");
//    codeOwner.setOwnedRefCode("REF123");
//    codeOwner.setPointsWallet(ownerWallet);
//    ownerWallet.setUser(codeOwner);
//
//    when(usersService.getUserByOwnedCode("REF123")).thenReturn(codeOwner);
//    when(pointsWalletService.getPointsWallet(codeOwner)).thenReturn(ownerWallet);
//    doThrow(new RuntimeException("PointsTrx save failed")).when(pointsTrxService).savePointsTrx(any(PointsTrx.class));
//
//    // Act & Assert
//    assertThrows(RuntimeException.class, () -> listener.useRefCode(user, "REF123"));
//    verify(pointsWalletService, times(1)).savePointsWallet(any());
//    verify(pointsTrxService, times(1)).savePointsTrx(any());
//    verify(voucherService, never()).saveVoucher(any());
//    verify(referralCodeUsageService, never()).saveReferralCodeUsage(any());
//  }
//
//  @Test
//  void testUseRefCode_PointsWalletServiceException() {
//    // Arrange
//    user.setRefCodeUsed("REF123");
//
//    PointsWallet ownerWallet = new PointsWallet();
//    ownerWallet.setId(2L);
//    ownerWallet.setPoints(0);
//
//    Users codeOwner = new Users();
//    codeOwner.setId(2L);
//    codeOwner.setUsername("codeOwner");
//    codeOwner.setOwnedRefCode("REF123");
//    codeOwner.setPointsWallet(ownerWallet);
//    ownerWallet.setUser(codeOwner);
//
//    when(usersService.getUserByOwnedCode("REF123")).thenReturn(codeOwner);
//    when(pointsWalletService.getPointsWallet(codeOwner)).thenReturn(ownerWallet);
//    doThrow(new RuntimeException("PointsWallet save failed")).when(pointsWalletService).savePointsWallet(any());
//
//    // Act & Assert
//    assertThrows(RuntimeException.class, () -> listener.useRefCode(user, "REF123"));
//    verify(pointsWalletService, times(1)).savePointsWallet(any());
//    verify(pointsTrxService, never()).savePointsTrx(any());
//    verify(voucherService, never()).saveVoucher(any());
//    verify(referralCodeUsageService, never()).saveReferralCodeUsage(any());
//  }
//
//  @Test
//  void testUseRefCode_ReferralCodeUsageServiceException() {
//    // Arrange
//    user.setRefCodeUsed("REF123");
//
//    PointsWallet ownerWallet = new PointsWallet();
//    ownerWallet.setId(2L);
//    ownerWallet.setPoints(0);
//
//    Users codeOwner = new Users();
//    codeOwner.setId(2L);
//    codeOwner.setUsername("codeOwner");
//    codeOwner.setOwnedRefCode("REF123");
//    codeOwner.setPointsWallet(ownerWallet);
//    ownerWallet.setUser(codeOwner);
//
//    when(usersService.getUserByOwnedCode("REF123")).thenReturn(codeOwner);
//    when(pointsWalletService.getPointsWallet(codeOwner)).thenReturn(ownerWallet);
//    doThrow(new RuntimeException("Saving referral code usage has failed :(")).when(referralCodeUsageService).saveReferralCodeUsage(any());
//
//    // Act & Assert
//    assertThrows(RuntimeException.class, () -> listener.useRefCode(user, "REF123"));
//  }
//}