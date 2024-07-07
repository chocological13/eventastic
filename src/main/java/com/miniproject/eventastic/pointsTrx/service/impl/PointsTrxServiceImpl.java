package com.miniproject.eventastic.pointsTrx.service.impl;

import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsTrx.repository.PointsTrxRepository;
import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class PointsTrxServiceImpl implements PointsTrxService {

  private final PointsTrxRepository pointsTrxRepository;

  @Override
  public void savePointsTrx(PointsTrx pointsTrx) {
    pointsTrxRepository.save(pointsTrx);
  }

  // call this in users
  @Override
  public Set<PointsTrx> getPointsTrx(PointsWallet pointsWallet) {
    return pointsTrxRepository.findByPointsWallet(pointsWallet);
  }
}
