package com.miniproject.eventastic.pointsWallet.repository;

import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.users.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsWalletRepository extends JpaRepository<PointsWallet, Long> {
  Optional<PointsWallet> findByUser(Users user);
}
