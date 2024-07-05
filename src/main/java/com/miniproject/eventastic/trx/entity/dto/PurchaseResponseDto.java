package com.miniproject.eventastic.trx.entity.dto;

import com.miniproject.eventastic.ticket.entity.dto.TrxIssuedTicketDto;
import com.miniproject.eventastic.ticketType.entity.dto.trx.TrxTicketTypeResponseDto;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.voucher.entity.dto.trx.TrxVoucherResponseDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class PurchaseResponseDto {

  private Long id;
  private String attendee;
  private String eventTitle;
  private TrxTicketTypeResponseDto ticketType;
  private Integer ticketQty;
  private BigDecimal initialPrice;
  private TrxVoucherResponseDto voucher;
  private Integer pointsUsed;
  private BigDecimal totalPrice;
  private String paymentMethod;
  private Instant trxDate;
  private Set<TrxIssuedTicketDto> ticketSet;

  public PurchaseResponseDto(Trx trx) {
    this.id = trx.getId();
    this.attendee = trx.getUser().getUsername();
    this.eventTitle = trx.getEvent().getTitle();
    this.ticketType = new TrxTicketTypeResponseDto(trx.getTicketType());
    this.ticketQty = trx.getQty();
    this.initialPrice = trx.getInitialAmount();
    this.voucher = new TrxVoucherResponseDto(trx.getVoucher());
    this.pointsUsed = trx.getPointsWallet().getPoints();
    this.totalPrice = trx.getTotalAmount();
    this.paymentMethod = trx.getPayment().getPaymentMethod();
    this.trxDate = trx.getTrxDate();
    this.ticketSet = trx.getTickets().stream()
        .map(TrxIssuedTicketDto::new)
        .collect(Collectors.toSet());
  }

  public PurchaseResponseDto toDto(Trx trx) {
    return new PurchaseResponseDto(trx);
  }
}
