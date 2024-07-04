package com.miniproject.eventastic.referralCodeUsage.repository;

import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.composite.ReferralCodeUsageId;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsersDto;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUseCountDto;
import com.miniproject.eventastic.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralCodeUsageRepository extends JpaRepository<ReferralCodeUsage, ReferralCodeUsageId> {

  // query for the use count of each code owner's code
  @Query("""
      SELECT new com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUseCountDto(u.username, COUNT(r.usedBy.id)) 
      FROM ReferralCodeUsage r
      INNER JOIN Users u ON r.codeOwner.id = u.id
      WHERE u = :user
      GROUP BY u.username, r.usedBy.id
      """)
  ReferralCodeUseCountDto countReferralCodeUsageWhereOwnerIs(@Param("user") Users user);

  // query for who has used code owner's code
  @Query("""
      SELECT new com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsersDto(r.usedBy.id, r.usedBy.username, r.usedAt)
      FROM ReferralCodeUsage r
      WHERE r.codeOwner = :user
      """)
  ReferralCodeUsersDto findReferralCodeUsersWhereOwnerIs(@Param("user") Users user);
}
