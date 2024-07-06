package com.miniproject.eventastic.trx.controller;

import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.trx.entity.dto.TrxPurchaseRequestDto;
import com.miniproject.eventastic.trx.entity.dto.response.TrxPurchaseResponseDto;
import com.miniproject.eventastic.trx.service.TrxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trx")
public class TrxController {

  private final TrxService trxService;

  @PostMapping("/purchase")
  ResponseEntity<Response<TrxPurchaseResponseDto>> createVoucher(@RequestBody TrxPurchaseRequestDto requestDto) {
    Trx trx = trxService.purchaseTicket(requestDto);
    return Response.successfulResponse(HttpStatus.CREATED.value(),
        "Purchase successful! We'll see you at " + trx.getEvent().getTitle() + "! We hope you have an EVENTASTIC "
        + "day!!", new TrxPurchaseResponseDto(trx, requestDto));
  }

}
