package com.miniproject.eventastic.voucher.service.impl;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.exceptions.trx.VoucherInvalidException;
import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
import com.miniproject.eventastic.exceptions.user.DuplicateCredentialsException;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.VoucherUsage;
import com.miniproject.eventastic.voucher.entity.VoucherUsageId;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateEventVoucherRequestDto;
import com.miniproject.eventastic.voucher.repository.VoucherRepository;
import com.miniproject.eventastic.voucher.repository.VoucherUsageRepository;
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
public class VoucherServiceImpl implements VoucherService {

  private final VoucherRepository voucherRepository;
  private final VoucherUsageRepository voucherUsageRepository;

  @Override
  public void saveVoucher(Voucher voucher) {
    voucherRepository.save(voucher);
  }

  @Override
  @Transactional
  public Voucher useVoucher(String voucherCode, Users user) {
    Voucher voucher = voucherRepository.findByCodeAndIsActiveTrue(voucherCode);

    // * check validity
    if (voucher == null) {
      throw new VoucherNotFoundException("Voucher with code " + voucherCode + " not found or is no longer active!");
    }

    // check if it's a for all voucher or for a specific user voucher
    if (voucher.getAwardee() != null && !voucher.getAwardee().getId().equals(user.getId())) {
      throw new VoucherInvalidException("This voucher isn't meant for you :(");
    }
    // check expiry
    Instant now = Instant.now();
    if (voucher.getExpiresAt().isBefore(now)) {
      voucher.setIsActive(false);
      voucher.setExpiresAt(now);
      voucherRepository.save(voucher);
      throw new VoucherInvalidException("Voucher with code " + voucherCode + " is expired!!");
    }
    // check if it's available
    if (voucher.getUseLimit() > 0) {
      voucher.setUseLimit(voucher.getUseLimit() - 1);
      if (voucher.getUseLimit() <= 0) {
        voucher.setIsActive(false);
        voucher.setDeactivatedAt(now);
      }
      voucherRepository.save(voucher);
    } else {
      throw new VoucherInvalidException("Voucher with code " + voucherCode + " is all used up!");
    }

    // check if voucher has been used by user before
    VoucherUsageId voucherUsageId = new VoucherUsageId(user.getId(), voucher.getId());

    VoucherUsage checkUsage = voucherUsageRepository.findById(voucherUsageId).orElse(null);
    if (checkUsage != null) {
      throw new VoucherInvalidException("You've used this voucher before!!");
    }

    // set usage history
    VoucherUsage voucherUsage = new VoucherUsage();
    voucherUsage.setId(voucherUsageId);
    voucherUsage.setUser(user);
    voucherUsage.setVoucher(voucher);
    voucherUsage.setUsedAt(now);
    voucherUsageRepository.save(voucherUsage);

    return voucher;
  }

  @Override
  public Voucher getVoucher(String code) {
    return voucherRepository.findByCode(code.toUpperCase())
        .orElseThrow(() -> new VoucherNotFoundException(code + " not found!"));
  }

  @Override
  public Voucher getVoucherByAwardee(Users user) {
    return voucherRepository.findByAwardeeAndIsActiveTrue(user);
  }

  @Override
  @Transactional
  public Voucher createEventVoucher(Users organizer, Event event, CreateEventVoucherRequestDto requestDto)
      throws AccessDeniedException {
    /* * check for voucher code and see if it already exists and is still valid for use
     * if voucher exists, but is not active (expired and use limit = 0),
     * reference it to the new voucher
     * this is so that later we can reference in the usage history without duplicate history
     * of the older voucher  */

    // * 1. verify user and get event
    if (!organizer.getIsOrganizer() && !organizer.getUsername().equals("strwbry")) {
      throw new AccessDeniedException("Only organizers and admins can create a voucher.");
    }

    // * 2. check if voucher exists and is active (will throw exception)
    String voucherCode = requestDto.getCode().toUpperCase();
    Voucher existingVoucher = voucherRepository.findByCodeAndIsActiveTrue(voucherCode);
    if (existingVoucher != null) {
      throw new DuplicateCredentialsException("Voucher with this code already exists! Please choose a different "
                                              + "voucher code!");
    }

    // * 3. init new voucher
    // time set up
    Instant now = Instant.now();
    int validity = requestDto.getValidity();
    ZonedDateTime endOfDay = ZonedDateTime.now().with(LocalTime.MAX);
    Instant expiresAt = endOfDay.toInstant().plus(validity, ChronoUnit.DAYS);

    Voucher newVoucher = new Voucher();
    newVoucher.setCode(voucherCode);
    newVoucher.setOrganizer(organizer);
    newVoucher.setEvent(event);
    newVoucher.setDescription(requestDto.getDescription());
    newVoucher.setPercentDiscount(requestDto.getPercentDiscount());
    newVoucher.setCreatedAt(Instant.now());
    newVoucher.setExpiresAt(expiresAt);
    newVoucher.setUseLimit(requestDto.getUseLimit() != null ? requestDto.getUseLimit() : 100); // default useLimit is
    // 100 if not given by organizer
    newVoucher.setIsActive(true);

    // * 4. check if voucher exists and is inactive, set this to originalVoucher
    // order by createdAt desc = will get the inactive voucher that is created the most recent
    Voucher inactiveVoucher = voucherRepository.findByCodeAndIsActiveFalseOrderByCreatedAtDesc(voucherCode);
    if (inactiveVoucher != null) {
      newVoucher.setOriginalVoucher(inactiveVoucher);
    }

    voucherRepository.save(newVoucher);
    return newVoucher;
  }


  @Override
  public List<Voucher> getAwardeesVouchers(Users user) {
    Long userId = user.getId();
    List<Voucher> voucherList = voucherRepository.findByAwardeeIdAndIsActiveTrue(userId);
    if (voucherList.isEmpty()) {
      throw new VoucherNotFoundException("You currently have no active vouchers.");
    }
    return voucherList;
  }

  // this will be called under event
  @Override
  public List<Voucher> getEventVouchers(Long eventId) {
    List<Voucher> voucherList = voucherRepository.findByEventIdAndIsActiveTrue(eventId);
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

  @Override
  public void saveVoucherUsage(VoucherUsage voucherUsage) {
    voucherUsageRepository.save(voucherUsage);
  }


}
