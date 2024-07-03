package com.miniproject.eventastic.users.listener;

import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.repository.PointsWalletRepository;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.event.UserRegistrationEvent;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegistrationListener {

  private final PointsWalletRepository pointsWalletRepository;

  @EventListener
  @Transactional
  public void handleUserRegistrationEvent(UserRegistrationEvent event) {
    Users user = event.getUser();
    PointsWallet pointsWallet = new PointsWallet();
    pointsWallet.setUser(user);
    pointsWallet.setPoints(0);
    pointsWalletRepository.save(pointsWallet);
    user.setPointsWallet(pointsWallet);
  }
}
