package com.miniproject.eventastic.voucher.controller;

import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateEventVoucherResponseDto;
import com.miniproject.eventastic.voucher.service.VoucherService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vouchers")
public class VoucherController {

  private final VoucherService voucherService;

  @GetMapping
  public ResponseEntity<Response<List<CreateEventVoucherResponseDto>>> getVouchersForAllUsers() {
    try {
      List<Voucher> voucherList = voucherService.getVouchersForAllUsers();
      List<CreateEventVoucherResponseDto> responseDtos = voucherList.stream()
          .map(CreateEventVoucherResponseDto::new)
          .toList();
      return Response.successfulResponse(HttpStatus.OK.value(), "Displaying vouchers available for all users",
          responseDtos);
    } catch (VoucherNotFoundException e) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
  }

}
