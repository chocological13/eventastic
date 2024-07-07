package com.miniproject.eventastic.organizerWalletTrx.service.impl;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.exceptions.trx.OrganizerWalletNotFound;
import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
import com.miniproject.eventastic.organizerWalletTrx.entity.OrganizerWalletTrx;
import com.miniproject.eventastic.organizerWalletTrx.repository.OrganizerWalletTrxRepository;
import com.miniproject.eventastic.organizerWalletTrx.service.OrganizerWalletTrxService;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.users.entity.Users;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class OrganizerWalletTrxServiceImpl implements OrganizerWalletTrxService {

  private final OrganizerWalletTrxRepository organizerWalletTrxRepository;
  private final OrganizerWalletService organizerWalletService;

  @Override
  public void save(OrganizerWalletTrx organizerWalletTrx) {
    organizerWalletTrxRepository.save(organizerWalletTrx);
  }

  @Override
  public Set<OrganizerWalletTrx> getByOrganizer(Users organizer) {
    Set<OrganizerWalletTrx> organizerWalletTrxes = organizerWalletTrxRepository.findByOrganizerWallet_Organizer(
        organizer);
    if (organizerWalletTrxes == null) {
      throw new OrganizerWalletNotFound("Wallet not found!! Or are you an impostor??");
    } else {
      return organizerWalletTrxes;
    }
  }

  @Override
  public void sendPayout(Trx trx) {
    Users organizer = trx.getEvent().getOrganizer();
    OrganizerWallet organizerWallet = organizerWalletService.getWalletByOrganizer(organizer);
    OrganizerWalletTrx payout = new OrganizerWalletTrx();
    if (organizerWallet == null) {
      throw new OrganizerWalletNotFound("Wallet not found!! Or are you an impostor??");
    } else {
      BigDecimal serviceFee = BigDecimal.valueOf(0.02);
      BigDecimal totalAmount = trx.getTotalAmount();
      BigDecimal payoutAmount = totalAmount.subtract(totalAmount.multiply(serviceFee));

      payout.setServiceFee(serviceFee);
      payout.setTrx(trx);
      payout.setTrxType("Payout");
      payout.setOrganizerWallet(organizerWallet);
      payout.setAmount(payoutAmount);
      organizerWalletTrxRepository.save(payout);

      organizerWallet.setBalance(organizerWallet.getBalance().add(payout.getAmount()));
      organizerWalletService.saveWallet(organizerWallet);
    }
  }
}
