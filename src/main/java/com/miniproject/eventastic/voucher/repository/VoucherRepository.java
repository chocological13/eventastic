package com.miniproject.eventastic.voucher.repository;

import com.miniproject.eventastic.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

}
