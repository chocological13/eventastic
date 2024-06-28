package com.miniproject.eventastic.referralCodeUsage.repository;

import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.composite.ReferralCodeUsageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralCodeUsageRepository extends JpaRepository<ReferralCodeUsage, ReferralCodeUsageId> {

}
