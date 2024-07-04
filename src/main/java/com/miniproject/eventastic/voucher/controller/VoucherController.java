package com.miniproject.eventastic.voucher.controller;

import com.miniproject.eventastic.exceptions.VoucherNotFoundException;
import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.VoucherRequestDto;
import com.miniproject.eventastic.voucher.entity.dto.VoucherResponseDto;
import com.miniproject.eventastic.voucher.service.VoucherService;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;
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
  public ResponseEntity<Response<VoucherResponseDto>> createVoucher(@RequestBody VoucherRequestDto voucherRequestDto)
      throws AccessDeniedException {
    VoucherResponseDto newVoucher = new VoucherResponseDto(voucherService.createVoucher(voucherRequestDto));
    return Response.successfulResponse(HttpStatus.CREATED.value(), "New Voucher Created!", newVoucher);
  }

  @GetMapping("/me")
  public ResponseEntity<Response<List<VoucherResponseDto>>> getAwardeesVoucher() {
    try {
      List<Voucher> voucherList = voucherService.getAwardeesVouchers();
      List<VoucherResponseDto> responseDtos = voucherList.stream()
          .map(VoucherResponseDto::toDto)
          .toList();
      return Response.successfulResponse(HttpStatus.FOUND.value(), "Displaying your vouchers..", responseDtos);
    } catch (VoucherNotFoundException e) {
      return Response.failedResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }
  }

}
