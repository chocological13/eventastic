package com.miniproject.eventastic.pointsWallet.service;

import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.entity.dto.PointsWalletResponseDto;
import com.miniproject.eventastic.users.entity.Users;

public interface PointsWalletService {

  void savePointsWallet(PointsWallet pointsWallet);

  void addPointsWallet(PointsWallet pointsWallet, Integer addPointsWallet);

  PointsWalletResponseDto getPointsWallet(Users loggedInUser);

}
