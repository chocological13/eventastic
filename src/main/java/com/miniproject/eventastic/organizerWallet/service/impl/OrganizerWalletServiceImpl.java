package com.miniproject.eventastic.organizerWallet.service.impl;

import com.miniproject.eventastic.exceptions.trx.OrganizerWalletNotFoundException;
import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.organizerWallet.repository.OrganizerWalletRepository;
import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
import com.miniproject.eventastic.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class OrganizerWalletServiceImpl implements OrganizerWalletService {

  private final OrganizerWalletRepository organizerWalletRepository;

  @Override
  public void saveWallet(OrganizerWallet organizerWallet) {
    organizerWalletRepository.save(organizerWallet);
  }

  @Override
  public OrganizerWallet getWalletByOrganizer(Users organizer) {
    OrganizerWallet orgWallet = organizerWalletRepository.findByOrganizer(organizer);
    if (orgWallet == null) {
      throw new OrganizerWalletNotFoundException("Organizer wallet not found! Or they may be an impostor..");
    }
    return orgWallet;
  }

}
