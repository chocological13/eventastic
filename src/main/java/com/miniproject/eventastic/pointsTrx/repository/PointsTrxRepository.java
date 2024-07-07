package com.miniproject.eventastic.pointsTrx.repository;

import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointsTrxRepository extends JpaRepository<PointsTrx, Long> {

  Set<PointsTrx> findByPointsWallet(PointsWallet pointsWallet);

}
