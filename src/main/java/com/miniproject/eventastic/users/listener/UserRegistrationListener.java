package com.miniproject.eventastic.users.listener;


import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.repository.PointsWalletRepository;
import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.composite.ReferralCodeUsageId;
import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.event.UserRegistrationEvent;
import com.miniproject.eventastic.users.service.UsersService;
import jakarta.transaction.Transactional;
import java.time.Instant;
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
  @EventListener
  @Transactional
  public void handleUserRegistrationEvent(UserRegistrationEvent event) {
    Users user = event.getUser();

    // * init points wallet
    PointsWallet pointsWallet = new PointsWallet();
    pointsWallet.setUser(user);
    pointsWallet.setPoints(0);
    pointsWalletService.savePointsWallet(pointsWallet);
    user.setPointsWallet(pointsWallet);

    // * Generate and assign referral code
    String ownedReferralCode = UUID.randomUUID().toString().substring(0, 7);
    user.setOwnedRefCode(ownedReferralCode);
    usersService.saveUser(user);

    // * Process referral code if used
    String refCodeUsed = event.getUser().getRefCodeUsed();
    Users owner = usersService.getUserByOwnedCode(refCodeUsed);
    if (owner != null) {
      // ** Add points to the new user's wallet
      pointsWallet.setPoints(pointsWallet.getPoints() + 10000);
      pointsWalletService.savePointsWallet(pointsWallet);

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
