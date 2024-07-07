package com.miniproject.eventastic.pointsTrx.service;

import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import java.util.Set;

public interface PointsTrxService {

  void savePointsTrx(PointsTrx pointsTrx);

  Set<PointsTrx> getPointsTrx(PointsWallet pointsWallet);
}
