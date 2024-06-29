package com.miniproject.eventastic.event.entity.dto.createEvent;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.ticketType.entity.dto.TicketTypeDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Data;

@Data
public class CreateEventRequestDto {

  @NotEmpty
  private String title;

  @NotEmpty
  private String description;

  @NotNull
  private Event.EventCategory eventCategory;

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

  private boolean isFree;

  @NotNull
  private Set<TicketTypeDto> ticketTypes;

  public Event dtoToEvent(CreateEventRequestDto eventRequestDto) {

    Event event = new Event();
    event.setTitle(eventRequestDto.title);
    event.setDescription(eventRequestDto.description);
    event.setEventCategory(eventRequestDto.eventCategory);
    event.setLocation(eventRequestDto.location);
    event.setVenue(eventRequestDto.venue);
    event.setEventDate(eventRequestDto.eventDate);
    event.setStartTime(eventRequestDto.startTime);
    event.setEndTime(eventRequestDto.endTime);
    event.setIsFree(eventRequestDto.isFree);
    return event;

  }
}
