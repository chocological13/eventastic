package com.miniproject.eventastic.trx.repository;

import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrxRepository extends JpaRepository<Trx, Long> {
 Page<Trx> findTrxByEvent_Organizer(Users organizer, Pageable pageable);
}
