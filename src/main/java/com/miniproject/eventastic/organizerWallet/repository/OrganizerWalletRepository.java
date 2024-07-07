package com.miniproject.eventastic.organizerWallet.repository;

import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerWalletRepository extends JpaRepository<OrganizerWallet, Long> {

  OrganizerWallet findByOrganizer(Users organizer);
}
