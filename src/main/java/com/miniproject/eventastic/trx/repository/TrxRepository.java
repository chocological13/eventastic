package com.miniproject.eventastic.trx.repository;

import com.miniproject.eventastic.dashboard.dto.DailyStatisticsDto;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.users.entity.Users;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrxRepository extends JpaRepository<Trx, Long> {
 Page<Trx> findTrxByEvent_Organizer(Users organizer, Pageable pageable);

 @Query(
     """
         SELECT new com.miniproject.eventastic.dashboard.dto.DailyStatisticsDto(
         t.trxDate,
         COUNT(t),
         SUM(t.totalAmount),
         SUM(owt.amount),
         SUM(t.qty)
         )
         FROM Trx t
         JOIN t.organizerWalletTrx owt
         WHERE t.event.organizer = :organizer
         AND t.trxDate BETWEEN :startDate AND :endDate
         GROUP BY t.trxDate
         """
 )
 List<DailyStatisticsDto> getDailyStatistics(Users organizer, @Param("startDate") Instant startDate,
     @Param("endDate")Instant endDate);
}
