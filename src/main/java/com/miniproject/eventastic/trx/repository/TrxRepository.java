package com.miniproject.eventastic.trx.repository;

import com.miniproject.eventastic.trx.entity.Trx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrxRepository extends JpaRepository<Trx, Long> {

}
