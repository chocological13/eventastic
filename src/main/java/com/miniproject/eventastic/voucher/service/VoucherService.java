package com.miniproject.eventastic.voucher.service;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.VoucherUsage;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateEventVoucherRequestDto;
import java.util.List;

public interface VoucherService {

  void saveVoucher(Voucher voucher);

  Voucher useVoucher(String voucherCode, Users user);

  Voucher getVoucher(String code);

  Voucher getVoucherByAwardee(Users user);

  Voucher createEventVoucher(Users organizer, Event event, CreateEventVoucherRequestDto requestDto);

  List<Voucher> getAwardeesVouchers(Users user);

  List<Voucher> getEventVouchers(Long eventId);

  List<Voucher> getVouchersForAllUsers();

  // Region - For usage history
  void saveVoucherUsage (VoucherUsage voucherUsage);
}
