package com.miniproject.eventastic.organizerWalletTrx.service;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.organizerWalletTrx.entity.OrganizerWalletTrx;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.users.entity.Users;
import java.util.Set;

public interface OrganizerWalletTrxService {
void save(OrganizerWalletTrx organizerWalletTrx);

Set<OrganizerWalletTrx> getByOrganizer(Users organizer);

void sendPayout(Trx trx);
}
