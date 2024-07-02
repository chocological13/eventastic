package com.miniproject.eventastic.event.entity.dto;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.ticketType.entity.dto.TicketTypeDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventResponseDto {

  @NotNull
  private String organizer;

  @NotEmpty
  private String title;

  @NotEmpty
  private String description;

  @NotEmpty
  private String location;

  @NotEmpty
  private String venue;

  @NotNull
  private LocalDate eventDate;

  @NotNull
  private LocalTime startTime;

  @NotNull
  private LocalTime endTime;

  @NotNull
  private Event.EventCategory eventCategory;

  @NotNull
  private Boolean isFree;

  private int seatLimit;
  private int availableSeat;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @NotEmpty
  private Set<TicketTypeDto> ticketTypes;


  public EventResponseDto(Event event) {
    // Populate fields from the Event entity
    this.organizer = event.getOrganizer().getUsername();
    this.title = event.getTitle();
    this.description = event.getDescription();
    this.eventCategory = event.getEventCategory();
    this.location = event.getLocation();
    this.venue = event.getVenue();
    this.eventDate = event.getEventDate();
    this.startTime = event.getStartTime();
    this.endTime = event.getEndTime();
    this.isFree = event.getIsFree();
    this.seatLimit = event.getSeatLimit();
    this.availableSeat = event.getAvailableSeat();
    this.createdAt = LocalDateTime.from(event.getCreatedAt());
    this.updatedAt = LocalDateTime.from(event.getUpdatedAt());
    this.ticketTypes = event.getTicketTypes().stream()
        .map(TicketTypeDto::new)
        .collect(Collectors.toSet());
  }

  public static EventResponseDto toEventResponseDto(Event event) {
    return new EventResponseDto(event);
  }

}
