package com.miniproject.eventastic.voucher.repository;

import com.miniproject.eventastic.voucher.entity.VoucherUsage;
import com.miniproject.eventastic.voucher.entity.VoucherUsageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, VoucherUsageId> {

}
