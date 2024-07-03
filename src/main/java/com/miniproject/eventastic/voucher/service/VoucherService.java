package com.miniproject.eventastic.voucher.service;

import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.VoucherRequestDto;
import com.miniproject.eventastic.voucher.entity.dto.VoucherResponseDto;

public interface VoucherService {

  Voucher createVoucher(VoucherRequestDto voucherRequestDto);
}
