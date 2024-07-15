package com.miniproject.eventastic.trx.entity.dto;

import com.miniproject.eventastic.ticket.entity.dto.TrxIssuedTicketDto;
import com.miniproject.eventastic.ticketType.entity.dto.trx.TrxTicketTypeResponseDto;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.trx.metadata.dto.TrxPaymentResponseDto;
import com.miniproject.eventastic.voucher.entity.dto.trx.TrxVoucherResponseDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class TrxPurchaseResponseDto {

  private Long id;
  private String attendee;
  private String eventTitle;
  private TrxTicketTypeResponseDto ticketType;
  private Integer ticketQty;
  private BigDecimal initialPrice;
  private TrxVoucherResponseDto voucher;
  private Boolean usingPoints;
  private Integer promoPercent;
  private BigDecimal totalPrice;
  private Boolean isPaid;
  private TrxPaymentResponseDto paymentMethod;
  private Instant trxDate;
  private Set<TrxIssuedTicketDto> ticketSet;

  public TrxPurchaseResponseDto(Trx trx, TrxPurchaseRequestDto requestDto) {
    this.id = trx.getId();
    this.attendee = trx.getUser().getUsername();
    this.eventTitle = trx.getEvent().getTitle();
    this.ticketType = new TrxTicketTypeResponseDto(trx.getTicketType());
    this.ticketQty = trx.getQty();
    this.initialPrice = trx.getInitialAmount();
    this.voucher = trx.getVoucher() != null ? new TrxVoucherResponseDto(trx.getVoucher()) : null;
    this.usingPoints = requestDto.getUsingPoints();
    this.promoPercent = trx.getEvent().getPromoPercent() != null ? trx.getEvent().getPromoPercent() : null;
    this.totalPrice = trx.getTotalAmount();
    this.isPaid = trx.getIsPaid();
    this.paymentMethod = new TrxPaymentResponseDto(trx.getPayment());
    this.trxDate = trx.getTrxDate();
    this.ticketSet = trx.getTickets().stream()
        .map(TrxIssuedTicketDto::new)
        .collect(Collectors.toSet());
  }

  public TrxPurchaseResponseDto toDto(Trx trx, TrxPurchaseRequestDto requestDto) {
    return new TrxPurchaseResponseDto(trx, requestDto);
  }
}
