package com.miniproject.eventastic.pointsWallet.service.impl;

import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.repository.PointsWalletRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointsExpirationService {

  private final PointsWalletRepository pointsWalletRepository;
  private final PointsTrxService pointsTrxService;

  @Transactional
  @Scheduled(cron = "${points.flush.cron:0 0 0 * * ?}")
  public void expirePoints() {
    Instant now = Instant.now();
    List<PointsWallet> expiredWallets = pointsWalletRepository.findExpiredPointsWallets(now);

    for (PointsWallet wallet : expiredWallets) {
      int expiredPoints = wallet.getPoints();
      wallet.setPoints(0);
      wallet.setUpdatedAt(now);

      PointsTrx expirationTrx = new PointsTrx();
      expirationTrx.setPointsWallet(wallet);
      expirationTrx.setPoints(-expiredPoints);
      expirationTrx.setDescription("Points expired");
      expirationTrx.setTrxType("Expiration");

      pointsWalletRepository.save(wallet);
      pointsTrxService.savePointsTrx(expirationTrx);
    }
  }
}
