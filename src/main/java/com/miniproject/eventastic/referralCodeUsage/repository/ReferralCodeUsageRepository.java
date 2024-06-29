package com.miniproject.eventastic.referralCodeUsage.repository;

import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.composite.ReferralCodeUsageId;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageByDto;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageOwnerDto;
import com.miniproject.eventastic.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralCodeUsageRepository extends JpaRepository<ReferralCodeUsage, ReferralCodeUsageId> {

  @Query("""
      SELECT new com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageOwnerDto(u.username, COUNT(r.usedBy.id)) 
      FROM ReferralCodeUsage r
      INNER JOIN Users u ON r.codeOwner.id = u.id
      WHERE u = :user
      GROUP BY u.username, r.usedBy.id
      """)
  ReferralCodeUsageOwnerDto findUsageSummaryByCodeOwner(@Param("user") Users user);

  @Query("""
      SELECT new com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsageByDto(r.usedBy.id, r.usedBy.username, r.usedAt)
      FROM ReferralCodeUsage r
      WHERE r.codeOwner = :user
      """)
  ReferralCodeUsageByDto findUsersByCodeOwner(@Param("user") Users user);
}
