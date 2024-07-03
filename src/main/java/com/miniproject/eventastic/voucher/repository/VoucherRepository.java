package com.miniproject.eventastic.voucher.repository;

import com.miniproject.eventastic.voucher.entity.Voucher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
  List<Voucher> findByAwardeeId(Long awardedTo);
  List<Voucher> findByEventId(Long eventId);
  List<Voucher> findByAwardeeIdIsNull();
}
