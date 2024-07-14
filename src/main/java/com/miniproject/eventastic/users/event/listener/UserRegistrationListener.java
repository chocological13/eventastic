package com.miniproject.eventastic.users.event.listener;


import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
import com.miniproject.eventastic.exceptions.user.UserNotFoundException;
import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.composite.ReferralCodeUsageId;
import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.event.UserRegistrationEvent;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserRegistrationListener {

  private final static int POINTS_REWARD = 10000;

  private final PointsWalletService pointsWalletService;
  private final UsersService usersService;
  private final ReferralCodeUsageService referralCodeUsageService;
  private final PointsTrxService pointsTrxService;
  private final OrganizerWalletService organizerWalletService;
  private final VoucherService voucherService;

  @EventListener
  @Transactional
  public void handleUserRegistrationEvent(UserRegistrationEvent event) throws RuntimeException {
    Users user = event.getUser();
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null in UserRegistrationEvent");
    }

    // * init points wallet
    initPointsWallet(user);

    // * init org wallet if org
    if (user.getIsOrganizer()) {
      initOrganizerWallet(user);
    }

    // * Generate and assign referral code
    String ownedReferralCode = generateReferralCode();
    user.setOwnedRefCode(ownedReferralCode);
    usersService.saveUser(user);

    // * Process referral code and update in points trx if used
    if (user.getRefCodeUsed() != null && !user.getRefCodeUsed().isEmpty()) {
      String refCodeUsed = user.getRefCodeUsed();
      useRefCode(user, refCodeUsed);
    }
  }

  public void initOrganizerWallet(Users user) {
    OrganizerWallet organizerWallet = new OrganizerWallet();
    organizerWallet.setOrganizer(user);
    organizerWallet.setBalance(BigDecimal.ZERO);
    organizerWalletService.saveWallet(organizerWallet);
    user.setOrganizerWallet(organizerWallet);
  }

  public void initPointsWallet(Users user) {
    PointsWallet pointsWallet = new PointsWallet();
    pointsWallet.setUser(user);
    pointsWallet.setPoints(0);
    pointsWalletService.savePointsWallet(pointsWallet);
    user.setPointsWallet(pointsWallet);
  }

  public String generateReferralCode() {
    String ownedReferralCode;
    do {
      ownedReferralCode = UUID.randomUUID().toString().substring(0, 7).toUpperCase();
    } while (checkRefCodeExist(ownedReferralCode));
    return ownedReferralCode;
  }

  public boolean checkRefCodeExist(String code) {
    return usersService.getUserByOwnedCode(code) != null;
  }

  public void useRefCode(Users user, String refCodeUsed) throws RuntimeException {

    Users owner = usersService.getUserByOwnedCode(refCodeUsed);
    if (owner != null) {
      // ** Add points to the code owner's wallet

      PointsWallet ownerPointsWallet = pointsWalletService.getPointsWallet(owner);

      ownerPointsWallet.setPoints(ownerPointsWallet.getPoints() + POINTS_REWARD);
      ownerPointsWallet.setAwardedAt(Instant.now());
      ownerPointsWallet.setExpiresAt(Instant.now().plus(90, ChronoUnit.DAYS));

      // update in points trx
      PointsTrx pointsTrx = new PointsTrx();
      pointsTrx.setPointsWallet(ownerPointsWallet);
      pointsTrx.setPoints(POINTS_REWARD);
      pointsTrx.setDescription("Referral code usage by new user");

      // ** Give voucher to user of the code
      // time set up
      ZonedDateTime endOfDay = ZonedDateTime.now().with(LocalTime.MAX);
      Instant expiresAt = endOfDay.toInstant().plus(90, ChronoUnit.DAYS);
      String code = generateVoucher();

      // init voucher
      Voucher newVoucher = new Voucher();
      newVoucher.setCode(code);
      newVoucher.setDescription("Thank you for using " + owner.getUsername() + "'s referral code!");
      newVoucher.setPercentDiscount(10);
      newVoucher.setCreatedAt(Instant.now());
      newVoucher.setExpiresAt(expiresAt);
      newVoucher.setAwardee(user);
      newVoucher.setUseLimit(1);
      newVoucher.setIsActive(true);

      // ** Log the usage of referral code
      ReferralCodeUsage usage = new ReferralCodeUsage(
          new ReferralCodeUsageId(user.getId(), owner.getId()),
          user,
          owner,
          Instant.now()
      );

      // make sure all the saves are executed, if not catch it
      try {
        pointsWalletService.savePointsWallet(ownerPointsWallet);
        pointsTrxService.savePointsTrx(pointsTrx);
        voucherService.saveVoucher(newVoucher);
        referralCodeUsageService.saveReferralCodeUsage(usage);
      } catch (Exception e) {
        log.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        throw new RuntimeException("Referral code usage have failed!");
      }

      log.info("Referral code from user {} was used by new user {}", owner.getUsername(), user.getUsername());
    } else {
      log.info("No referral code was used by new user {}", user.getUsername());
      throw new UserNotFoundException("No code found, make sure you've entered the right referral code");
    }
  }

  public String generateVoucher() {
    String code;
    // check if code exists and regenerate code until it isn't in database
    do {
      code = "REF10" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    } while (checkVoucher(code));
    return code;
  }

  @Transactional(dontRollbackOn = VoucherNotFoundException.class)
  public boolean checkVoucher(String code) {
    try {
      voucherService.getVoucher(code);
    } catch (VoucherNotFoundException e) {
      log.info("Intended to check voucher code {}", code);
      return false;
    }
    return true;
  }
}
