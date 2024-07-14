package com.miniproject.eventastic.voucher.service.impl;

import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.repository.VoucherRepository;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Data
@RequiredArgsConstructor
@Service
public class VoucherCleanupService {

  private final VoucherRepository voucherRepository;

  @Scheduled(cron = "${voucher.cleanup.cron:0 0 0 * * ?}")
  public void deactivateInvalidVouchers() {
    // this runs daily at midnight to deactivate voucher if it's expired

    Instant now = Instant.now();
    List<Voucher> expiredVoucher = voucherRepository.findByExpiresAtBeforeAndIsActiveTrue(now);
    List<Voucher> usedUpVoucher = voucherRepository.findByUseLimitLessThanEqualAndIsActiveTrue(0);

    expiredVoucher.forEach(v -> {
      v.setIsActive(false);
      v.setDeactivatedAt(now);
    });
    usedUpVoucher.forEach(v -> {
      v.setIsActive(false);
      v.setDeactivatedAt(now);
    });

    voucherRepository.saveAll(expiredVoucher);
    voucherRepository.saveAll(usedUpVoucher);
  }
}
