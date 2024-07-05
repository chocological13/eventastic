package com.miniproject.eventastic.voucher.entity.dto.create;

import com.miniproject.eventastic.voucher.entity.Voucher;
import java.time.Instant;
import java.util.Objects;
import lombok.Data;

@Data
public class CreateVoucherResponseDto {

  private String code;
  private String description;
  private String awardee;
  private String organizer;
  private String eventTitle;
  private Integer percentDiscount;
  private Integer useLimit;
  private Instant createdAt;
  private Instant expiresAt;

  public CreateVoucherResponseDto(Voucher voucher) {

    this.code = voucher.getCode();
    this.description = voucher.getDescription();
    this.awardee = voucher.getAwardee() != null ?
        voucher.getAwardee().getUsername() :
        "Available for all users!";
    this.organizer = !Objects.equals(voucher.getOrganizer().getUsername(), "strwbry") ?
        voucher.getOrganizer().getUsername() :
        "Presented to you by EVENTASTIC!";
    this.eventTitle = voucher.getEvent() != null ?
        voucher.getEvent().getTitle() :
        "Available for all events!";
    this.percentDiscount = voucher.getPercentDiscount();
    this.useLimit = voucher.getUseLimit();
    this.createdAt = voucher.getCreatedAt();
    this.expiresAt = voucher.getExpiresAt();
  }

  public CreateVoucherResponseDto toDto(Voucher voucher) {
    return new CreateVoucherResponseDto(voucher);
  }

}
