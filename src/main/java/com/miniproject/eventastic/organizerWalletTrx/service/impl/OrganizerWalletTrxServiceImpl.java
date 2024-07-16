package com.miniproject.eventastic.organizerWalletTrx.service.impl;

import com.miniproject.eventastic.exceptions.trx.OrganizerWalletNotFoundException;
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
  public Set<OrganizerWalletTrx> getByOrganizer(Users organizer) throws OrganizerWalletNotFoundException {
    Set<OrganizerWalletTrx> organizerWalletTrxes = organizerWalletTrxRepository.findByOrganizerWallet_Organizer(
        organizer);
    if (organizerWalletTrxes == null) {
      throw new OrganizerWalletNotFoundException("No transactions were made yet");
    } else {
      return organizerWalletTrxes;
    }
  }

  @Override
  public void sendPayout(Trx trx) throws OrganizerWalletNotFoundException {
    Users organizer = trx.getEvent().getOrganizer();
    OrganizerWallet organizerWallet = organizerWalletService.getWalletByOrganizer(organizer);
    OrganizerWalletTrx payout = new OrganizerWalletTrx();
    if (organizerWallet == null) {
      throw new OrganizerWalletNotFoundException("Wallet not found!! Or are you an impostor??");
    } else {
      BigDecimal totalAmount;

      // check if there's an organizer made up promotion, deduct that from the revenue
      if(trx.getEvent().getPromoPercent() == null) {
        totalAmount = trx.getInitialAmount();
      } else {
        BigDecimal promo =
            trx.getInitialAmount().multiply(BigDecimal.valueOf(trx.getEvent().getPromoPercent())).divide(BigDecimal.valueOf(100), 2,
                BigDecimal.ROUND_HALF_UP);
        totalAmount = trx.getInitialAmount().subtract(promo);
      }

      BigDecimal serviceFee = BigDecimal.valueOf(0.02);
      BigDecimal payoutAmount = totalAmount.subtract(totalAmount.multiply(serviceFee));

      payout.setServiceFee(serviceFee);
      payout.setTrx(trx);
      payout.setTrxType("Payout");
      payout.setDescription("Payout for transaction of " + trx.getEvent().getTitle());
      payout.setOrganizerWallet(organizerWallet);
      payout.setAmount(payoutAmount);
      organizerWalletTrxRepository.save(payout);

      organizerWallet.setBalance(organizerWallet.getBalance().add(payout.getAmount()));
      organizerWalletService.saveWallet(organizerWallet);
    }
  }
}
