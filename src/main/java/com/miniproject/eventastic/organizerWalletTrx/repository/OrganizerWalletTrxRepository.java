package com.miniproject.eventastic.organizerWalletTrx.repository;

import com.miniproject.eventastic.organizerWalletTrx.entity.OrganizerWalletTrx;
import com.miniproject.eventastic.users.entity.Users;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerWalletTrxRepository extends JpaRepository<OrganizerWalletTrx, Long> {

  Set<OrganizerWalletTrx> findByOrganizerWallet_Organizer(Users organizer);

}
