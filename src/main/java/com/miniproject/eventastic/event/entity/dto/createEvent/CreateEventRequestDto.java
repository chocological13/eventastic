package com.miniproject.eventastic.event.entity.dto.createEvent;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.ticketType.entity.dto.create.TicketTypeCreateRequestDto;
import com.miniproject.eventastic.voucher.entity.dto.create.CreateEventVoucherRequestDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequestDto {

  @NotEmpty
  private String title;
  @NotEmpty
  private String description;
  @NotNull
  private Long categoryId;
  @NotEmpty
  private String location;
  @NotEmpty
  private String venue;
  private String map;
  private Long imageId;
  @NotNull
  private LocalDate eventDate;
  @NotNull
  private LocalTime startTime;
  @NotNull
  private LocalTime endTime;
  @NotNull
  private Boolean isFree;
  @NotNull
  private Integer referralVoucherUsageLimit;
  private Integer promoPercent;
  private Integer promoDaysValidity; // this will be the amount of days added to the day of the creation of the event
  // and then converted to EOD of the day that it is added to
  private CreateEventVoucherRequestDto voucherRequestDto;
  private Set<TicketTypeCreateRequestDto> ticketTypeRequestDtos;

  public Event dtoToEvent(CreateEventRequestDto eventRequestDto) {
    Instant promoEndDate = null;
    if (promoPercent != null) {
      Instant now = Instant.now();
      int validity = eventRequestDto.promoDaysValidity == null ? 30 : eventRequestDto.promoDaysValidity;
      ZonedDateTime endOfDay = ZonedDateTime.now().with(LocalTime.MAX);
      promoEndDate = endOfDay.toInstant().plus(validity, ChronoUnit.DAYS);
    }

    Event event = new Event();
    event.setTitle(eventRequestDto.title);
    event.setDescription(eventRequestDto.description);
    event.setLocation(eventRequestDto.location);
    event.setVenue(eventRequestDto.venue);
    event.setMap(eventRequestDto.map);
    event.setEventDate(eventRequestDto.eventDate);
    event.setStartTime(eventRequestDto.startTime);
    event.setEndTime(eventRequestDto.endTime);
    event.setIsFree(eventRequestDto.isFree);
    event.setReferralVoucherUsageLimit(eventRequestDto.referralVoucherUsageLimit);
    event.setReferralVoucherUsageAvailability(eventRequestDto.referralVoucherUsageLimit);
    event.setPromoPercent((eventRequestDto.promoPercent != null && eventRequestDto.promoPercent.describeConstable()
        .isPresent()) ? eventRequestDto.promoPercent : null);
    event.setPromoEndDate(promoEndDate);

    return event;
  }
}
