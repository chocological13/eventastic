package com.miniproject.eventastic.users.event.listener;


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
public class UserRegistrationListener {

  private final PointsWalletService pointsWalletService;
  private final UsersService usersService;
  private final ReferralCodeUsageService referralCodeUsageService;
  private final PointsTrxService pointsTrxService;
  private final OrganizerWalletService organizerWalletService;
  private final VoucherService voucherService;

  @EventListener
  @Transactional
  public void handleUserRegistrationEvent(UserRegistrationEvent event) {
    Users user = event.getUser();

    // * init points wallet
    PointsWallet pointsWallet = initPointsWallet(user);

    // * init org wallet if org
    if (user.getIsOrganizer())
      initOrganizerWallet(user);

    // * Generate and assign referral code
    String ownedReferralCode = UUID.randomUUID().toString().substring(0, 7);
    user.setOwnedRefCode(ownedReferralCode);
    usersService.saveUser(user);

    // * Process referral code and update in points trx if used
    String refCodeUsed = event.getUser().getRefCodeUsed();
    useRefCode(user, refCodeUsed);
  }

  public void initOrganizerWallet(Users user) {
    OrganizerWallet organizerWallet = new OrganizerWallet();
    organizerWallet.setOrganizer(user);
    organizerWallet.setBalance(BigDecimal.ZERO);
    organizerWalletService.saveWallet(organizerWallet);
    user.setOrganizerWallet(organizerWallet);
  }

  public PointsWallet initPointsWallet(Users user) {
    PointsWallet pointsWallet = new PointsWallet();
    pointsWallet.setUser(user);
    pointsWallet.setPoints(0);
    pointsWalletService.savePointsWallet(pointsWallet);
    user.setPointsWallet(pointsWallet);
    return pointsWallet;
  }

  public void useRefCode(Users user, String refCodeUsed) {
    Users owner = usersService.getUserByOwnedCode(refCodeUsed);
    if (owner != null) {
      // ** Add points to the code owner's wallet
      Integer pointsReward = 10000;

      PointsWallet ownerPointsWallet = pointsWalletService.getPointsWallet(owner);

      ownerPointsWallet.setPoints(ownerPointsWallet.getPoints() + pointsReward);
      ownerPointsWallet.setAwardedAt(Instant.now());
      ownerPointsWallet.setExpiresAt(Instant.now().plus(90, ChronoUnit.DAYS));
      pointsWalletService.savePointsWallet(ownerPointsWallet);

      // update in points trx
      PointsTrx pointsTrx = new PointsTrx();
      pointsTrx.setPointsWallet(ownerPointsWallet);
      pointsTrx.setPoints(pointsReward);
      pointsTrx.setTrxType("Addition");
      pointsTrx.setDescription("Referral code usage by new user");
      pointsTrxService.savePointsTrx(pointsTrx);

      // ** Give voucher to user of the code
      // time set up
      ZonedDateTime endOfDay = ZonedDateTime.now().with(LocalTime.MAX);
      Instant expiresAt = endOfDay.toInstant().plus(90, ChronoUnit.DAYS);
      String code = "REF10" + UUID.randomUUID().toString().substring(0, 4);

      // check if code exists and regenerate code until it isn't in database
      while (voucherService.getVoucher(code) != null) {
        code = "REF10" + UUID.randomUUID().toString().substring(0, 4);
      }

      // init voucher
      Voucher newVoucher = new Voucher();
      newVoucher.setCode(code);
      newVoucher.setDescription("Thank you for using " + owner.getUsername() + "'s referral code!");
      newVoucher.setPercentDiscount(10);
      newVoucher.setCreatedAt(Instant.now());
      newVoucher.setExpiresAt(expiresAt);
      newVoucher.setAwardee(user);
      newVoucher.setUseLimit(1);
      voucherService.saveVoucher(newVoucher);

      // ** Log the usage of referral code
      ReferralCodeUsage usage = new ReferralCodeUsage(
          new ReferralCodeUsageId(user.getId(), owner.getId()),
          user,
          owner,
          Instant.now()
      );
      referralCodeUsageService.saveReferralCodeUsage(usage);

      log.info("Referral code from user {} was used by new user {}", owner.getUsername(), user.getUsername());
    } else {
      log.info("No referral code was used by new user {}", user.getUsername());
    }
  }
}
