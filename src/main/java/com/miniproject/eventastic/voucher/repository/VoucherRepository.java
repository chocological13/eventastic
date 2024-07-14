package com.miniproject.eventastic.voucher.repository;

import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.voucher.entity.Voucher;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

  Optional<Voucher> findByCode(String code);

  // TODO : change these to check for isActive instead
  List<Voucher> findByAwardeeIdAndIsActiveTrue(Long awardedTo);

  List<Voucher> findByEventIdAndIsActiveTrue(Long eventId);

  List<Voucher> findByAwardeeIdIsNullAndExpiresAtIsAfter(Instant now);

  Voucher findByAwardeeAndIsActiveTrue(Users user);

  Voucher findByCodeAndIsActiveTrue(String code);

  Voucher findByCodeAndIsActiveFalseOrderByCreatedAtDesc(String code);

  // for cron
  List<Voucher> findByExpiresAtBeforeAndIsActiveTrue(Instant now);

  List<Voucher> findByUseLimitLessThanEqualAndIsActiveTrue(int useLimit);

}
