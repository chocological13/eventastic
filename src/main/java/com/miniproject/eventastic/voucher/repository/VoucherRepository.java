package com.miniproject.eventastic.voucher.repository;

import com.miniproject.eventastic.voucher.entity.Voucher;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
  Optional<Voucher> findByCode(String code);
  List<Voucher> findByAwardeeIdAndExpiresAtIsAfter(Long awardedTo, Instant now);
  List<Voucher> findByEventIdAndExpiresAtIsAfter(Long eventId, Instant now);
  List<Voucher> findByAwardeeIdIsNullAndExpiresAtIsAfter(Instant now);
}
