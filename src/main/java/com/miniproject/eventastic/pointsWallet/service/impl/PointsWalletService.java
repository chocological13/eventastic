package com.miniproject.eventastic.pointsWallet.service.impl;

import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
import com.miniproject.eventastic.pointsWallet.repository.PointsWalletRepository;
import com.miniproject.eventastic.users.entity.Users;
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
    pointsWalletRepository.save(pointsWallet);
  }

  @Override
  public PointsWalletResponseDto getPointsWallet(Users loggedInUser) {
    Optional<PointsWallet> walletOptional = pointsWalletRepository.findByUser(loggedInUser);
    if (walletOptional.isPresent()) {
      PointsWallet pointsWallet = walletOptional.get();
      return new PointsWalletResponseDto(pointsWallet);
    }
    return null;
  }
}
