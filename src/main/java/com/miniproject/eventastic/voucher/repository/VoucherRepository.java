package com.miniproject.eventastic.voucher.repository;

import com.miniproject.eventastic.voucher.entity.Voucher;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
  List<Voucher> findByAwardeeIdAndExpiresAtIsAfter(Long awardedTo, Instant now);
  List<Voucher> findByEventIdAndExpiresAtIsAfter(Long eventId, Instant now);
  List<Voucher> findByAwardeeIdIsNullAndExpiresAtIsAfter(Instant now);
}
