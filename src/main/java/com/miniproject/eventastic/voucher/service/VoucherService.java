package com.miniproject.eventastic.voucher.service;

import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.VoucherRequestDto;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface VoucherService {

  Voucher createVoucher(VoucherRequestDto voucherRequestDto) throws AccessDeniedException;

  List<Voucher> getAwardeesVouchers();

  List<Voucher> getEventVouchers(Long eventId);

  List<Voucher> getVouchersForAllUsers();
}
