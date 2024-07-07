package com.miniproject.eventastic.organizerWallet.service;

import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.users.entity.Users;

public interface OrganizerWalletService {

  void saveWallet(OrganizerWallet organizerWallet);

  OrganizerWallet getWalletByOrganizer(Users organizer);
}
