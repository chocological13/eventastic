package com.miniproject.eventastic.voucher.controller;

import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateVoucherRequestDto;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateVoucherResponseDto;
import com.miniproject.eventastic.voucher.service.VoucherService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vouchers")
public class VoucherController {

  private final VoucherService voucherService;

  @PostMapping("/create")
  public ResponseEntity<Response<CreateVoucherResponseDto>> createVoucher(@RequestBody CreateVoucherRequestDto createVoucherRequestDto) {
    CreateVoucherResponseDto newVoucher = new CreateVoucherResponseDto(voucherService.createVoucher(createVoucherRequestDto));
    return Response.successfulResponse(HttpStatus.CREATED.value(), "New Voucher Created!", newVoucher);
  }

  @GetMapping
  public ResponseEntity<Response<List<CreateVoucherResponseDto>>> getVouchersForAllUsers() {
    try {
      List<Voucher> voucherList = voucherService.getVouchersForAllUsers();
      List<CreateVoucherResponseDto> responseDtos = voucherList.stream()
          .map(CreateVoucherResponseDto::new)
          .toList();
      return Response.successfulResponse(HttpStatus.OK.value(), "Displaying vouchers available for all users",
          responseDtos);
    } catch (VoucherNotFoundException e) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
  }

}
