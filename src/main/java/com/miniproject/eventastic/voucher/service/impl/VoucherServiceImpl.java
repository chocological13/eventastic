package com.miniproject.eventastic.voucher.service.impl;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateVoucherRequestDto;
import com.miniproject.eventastic.voucher.repository.VoucherRepository;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
@Transactional
public class VoucherServiceImpl implements VoucherService {

  private final VoucherRepository voucherRepository;
  private final UsersService usersService;
  private final EventService eventService;

  @Override
  public void saveVoucher(Voucher voucher) {
    voucherRepository.save(voucher);
  }

  @Override
  public Voucher getVoucher(String code) {
    return voucherRepository.findByCode(code.toUpperCase()).orElse(null);
  }

  @Override
  public Voucher createVoucher(CreateVoucherRequestDto createVoucherRequestDto) throws AccessDeniedException {
    // verify user
    Users loggedInUser = usersService.getCurrentUser();
    if (!loggedInUser.getIsOrganizer() && !loggedInUser.getUsername().equals("strwbry")) {
      throw new AccessDeniedException("Only organizers and admins can create a voucher.");
    }

    // time set up
    ZonedDateTime endOfDay = ZonedDateTime.now().with(LocalTime.MAX);
    Instant expiresAt = endOfDay.toInstant().plus(createVoucherRequestDto.getValidity(), ChronoUnit.DAYS);

    // init voucher
    Voucher newVoucher = new Voucher();
    newVoucher.setCode(createVoucherRequestDto.getCode());
    newVoucher.setDescription(createVoucherRequestDto.getDescription());
    newVoucher.setPercentDiscount(createVoucherRequestDto.getPercentDiscount());
    newVoucher.setCreatedAt(Instant.now());
    newVoucher.setExpiresAt(expiresAt);
    newVoucher.setOrganizer(loggedInUser);

    // see if voucher is for a user or specific to an event
    if (createVoucherRequestDto.getAwardeeId() != null) {
      Users awardedUser = usersService.getById(createVoucherRequestDto.getAwardeeId());
      newVoucher.setAwardee(awardedUser);
    } else {
      // limit usage only for global voucher, if user is null and useLimit is null, default useLimit is 100
      newVoucher.setUseLimit(createVoucherRequestDto.getUseLimit() != null ? createVoucherRequestDto.getUseLimit() : 100);
    }

    // see if voucher is tied to a specific event
    if (createVoucherRequestDto.getEventId() != null) {
      Event event = eventService.getEventById(createVoucherRequestDto.getEventId());
      newVoucher.setEvent(event);
    }

    voucherRepository.save(newVoucher);
    return newVoucher;
  }



  @Override
  public List<Voucher> getAwardeesVouchers() {
    Users loggedInUser = usersService.getCurrentUser();
    Long userId = loggedInUser.getId();
    List<Voucher> voucherList = voucherRepository.findByAwardeeIdAndExpiresAtIsAfter(userId, Instant.now());
    if (voucherList.isEmpty()) {
      throw new VoucherNotFoundException("You currently have no active vouchers.");
    }
    return voucherList;
  }

  // this will be called under event
  @Override
  public List<Voucher> getEventVouchers(Long eventId) {
    List<Voucher> voucherList = voucherRepository.findByEventIdAndExpiresAtIsAfter(eventId, Instant.now());
    if (voucherList.isEmpty()) {
      throw new VoucherNotFoundException("This event currently offers no vouchers :(");
    }
    return voucherList;
  }

  @Override
  public List<Voucher> getVouchersForAllUsers() {
    List<Voucher> voucherList = voucherRepository.findByAwardeeIdIsNullAndExpiresAtIsAfter(Instant.now());
    if (voucherList.isEmpty()) {
      throw new VoucherNotFoundException("There are no active global vouchers");
    }
    return voucherList;
  }


}
