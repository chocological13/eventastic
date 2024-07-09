package com.miniproject.eventastic.event.entity.dto;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.image.entity.dto.eventImage.EventImageResponseDto;
import com.miniproject.eventastic.ticketType.entity.dto.create.TicketTypeCreateResponseDto;
import java.time.Instant;
import java.time.LocalDate;
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

  private Long id;
  private String organizer;
  private String title;
  private String description;
  private String location;
  private String venue;
  private EventImageResponseDto image;
  private LocalDate eventDate;
  private LocalTime startTime;
  private LocalTime endTime;
  private String category;
  private Boolean isFree;
  private int seatLimit;
  private int availableSeat;
  private Instant createdAt;
  private Instant updatedAt;
  private Set<TicketTypeCreateResponseDto> ticketTypes;


  public EventResponseDto(Event event) {
    // Populate fields from the Event entity
    this.id = event.getId();
    this.organizer = event.getOrganizer().getUsername();
    this.title = event.getTitle();
    this.description = event.getDescription();
    this.category = event.getCategory().getName();
    this.location = event.getLocation();
    this.venue = event.getVenue();
    this.image = new EventImageResponseDto(event.getImage());
    this.eventDate = event.getEventDate();
    this.startTime = event.getStartTime();
    this.endTime = event.getEndTime();
    this.isFree = event.getIsFree();
    this.seatLimit = event.getSeatLimit();
    this.availableSeat = event.getAvailableSeat();
    this.createdAt = event.getCreatedAt();
    this.updatedAt = event.getUpdatedAt();
    this.ticketTypes = event.getTicketTypes().stream()
        .map(TicketTypeCreateResponseDto::new)
        .collect(Collectors.toSet());
  }

  public EventResponseDto toEventResponseDto(Event event) {
    return new EventResponseDto(event);
  }

}
