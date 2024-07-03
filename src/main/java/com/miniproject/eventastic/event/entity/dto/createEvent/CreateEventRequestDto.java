package com.miniproject.eventastic.event.entity.dto.createEvent;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.repository.ImageRepository;
import com.miniproject.eventastic.ticketType.entity.dto.TicketTypeRequestDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
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
  private Event.EventCategory eventCategory;

  @NotEmpty
  private String location;

  @NotEmpty
  private String venue;

  private Long imageId;

  @NotNull
  private LocalDate eventDate;

  @NotNull
  private LocalTime startTime;

  @NotNull
  private LocalTime endTime;

  @NotNull
  private Boolean isFree;

  private Set<TicketTypeRequestDto> ticketTypeRequestDtos;

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
