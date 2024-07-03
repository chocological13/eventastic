package com.miniproject.eventastic.voucher.service.impl;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.service.EventService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.dto.VoucherRequestDto;
import com.miniproject.eventastic.voucher.entity.dto.VoucherResponseDto;
import com.miniproject.eventastic.voucher.repository.VoucherRepository;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.util.Optional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
@Transactional
public class VoucherServiceImpl implements VoucherService {

  private final VoucherRepository voucherRepository;
  private final UsersService usersService;
  private final EventService eventService;

  @SneakyThrows
  @Override
  @Transactional
  public Voucher createVoucher(VoucherRequestDto voucherRequestDto) {
    // verify user
    Users loggedInUser = usersService.getCurrentUser();
    if (!loggedInUser.getIsOrganizer()) {
      throw new AccessDeniedException("Only organizers can create a voucher.");
    }

    // init voucher
    Voucher newVoucher = new Voucher();
    newVoucher.setCode(voucherRequestDto.getCode());
    newVoucher.setDescription(voucherRequestDto.getDescription());
    newVoucher.setDiscountPercentage(voucherRequestDto.getDiscountPercentage());
    voucherRepository.save(newVoucher);

    // see if voucher is for a user or specific to an event
    if (voucherRequestDto.getAwardedToId() != null) {
      Users awardedUser = usersService.getById(voucherRequestDto.getAwardedToId());
      newVoucher.setAwardedTo(awardedUser);
    }
    if (voucherRequestDto.getEventId() != null) {
      Event event = eventService.getEventById(voucherRequestDto.getEventId());
      newVoucher.setEvent(event);
    }

    voucherRepository.save(newVoucher);
    return newVoucher;
  }
}
