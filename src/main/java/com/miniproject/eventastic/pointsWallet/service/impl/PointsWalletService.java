package com.miniproject.eventastic.pointsWallet.service.impl;

import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
import com.miniproject.eventastic.pointsWallet.repository.PointsWalletRepository;
import com.miniproject.eventastic.users.entity.Users;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointsWalletService implements com.miniproject.eventastic.pointsWallet.service.PointsWalletService {

  private final PointsWalletRepository pointsWalletRepository;

  @Override
  public void savePointsWallet(PointsWallet pointsWallet) {
    pointsWalletRepository.save(pointsWallet);
  }

  @Override
  public void addPointsWallet(PointsWallet pointsWallet, Integer addPointsWallet) {
    pointsWallet.setPoints(pointsWallet.getPoints() + addPointsWallet);
    pointsWallet.setAwardedAt(Instant.now());
    pointsWallet.setExpiresAt(Instant.now().plus(90, ChronoUnit.DAYS));
    pointsWalletRepository.save(pointsWallet);
  }

  @Override
  public PointsWallet getPointsWallet(Users loggedInUser) {
    Optional<PointsWallet> walletOptional = pointsWalletRepository.findByUser(loggedInUser);
    if (walletOptional.isPresent()) {
      return walletOptional.get();
    }
    return null;
  }
}
