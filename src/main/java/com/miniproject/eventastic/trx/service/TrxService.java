package com.miniproject.eventastic.trx.service;

import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.trx.entity.dto.TrxPurchaseRequestDto;

public interface TrxService {

  Trx purchaseTicket(TrxPurchaseRequestDto requestDto);
}
