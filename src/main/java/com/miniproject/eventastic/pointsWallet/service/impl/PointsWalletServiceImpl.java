package com.miniproject.eventastic.pointsWallet.service.impl;

import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.repository.PointsWalletRepository;
import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointsWalletServiceImpl implements PointsWalletService {

  private final PointsWalletRepository pointsWalletRepository;

  @Override
  public void addPointsWallet(PointsWallet pointsWallet, Integer addPointsWallet) {
    pointsWallet.setPoints(pointsWallet.getPoints() + addPointsWallet);
    pointsWalletRepository.save(pointsWallet);
  }
}
