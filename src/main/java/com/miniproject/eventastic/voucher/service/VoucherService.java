package com.miniproject.eventastic.voucher.service;

import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateVoucherRequestDto;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface VoucherService {

  void saveVoucher(Voucher voucher);

  Voucher getVoucher(String code);

  Voucher createVoucher(CreateVoucherRequestDto createVoucherRequestDto) throws AccessDeniedException;

  List<Voucher> getAwardeesVouchers();

  List<Voucher> getEventVouchers(Long eventId);

  List<Voucher> getVouchersForAllUsers();
}
