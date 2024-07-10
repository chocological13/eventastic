package com.miniproject.eventastic.trx.event;

import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.trx.entity.dto.TrxPurchaseRequestDto;
import com.miniproject.eventastic.users.entity.Users;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TicketPurchasedEvent extends ApplicationEvent {

  private final Users user;
  private final Trx trx;
  private final TrxPurchaseRequestDto requestDto;

  public TicketPurchasedEvent(Object source, Users user, Trx trx, TrxPurchaseRequestDto requestDto) {
    super(source);
    this.user = user;
    this.trx = trx;
    this.requestDto = requestDto;
  }

}
