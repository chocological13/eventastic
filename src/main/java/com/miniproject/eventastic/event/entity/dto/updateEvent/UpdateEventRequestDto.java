package com.miniproject.eventastic.event.entity.dto.updateEvent;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.ticketType.entity.dto.update.TicketTypeUpdateRequestDto;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequestDto {

  private String title;
  private String description;
  private Long categoryId;
  private String location;
  private String venue;
  private String map;
  private Long imageId;
  private LocalDate eventDate;
  private LocalTime startTime;
  private LocalTime endTime;
  private Set<TicketTypeUpdateRequestDto> ticketTypeUpdates;

  public Event dtoToEvent(Event event, UpdateEventRequestDto updateDto) {
    event.setTitle(Optional.ofNullable(title).orElse(updateDto.getTitle()));
    event.setDescription(Optional.ofNullable(description).orElse(updateDto.getDescription()));
    event.setLocation(Optional.ofNullable(location).orElse(updateDto.getLocation()));
    event.setVenue(Optional.ofNullable(venue).orElse(updateDto.getVenue()));
    event.setEventDate(Optional.ofNullable(eventDate).orElse(updateDto.getEventDate()));
    event.setStartTime(Optional.ofNullable(startTime).orElse(updateDto.getStartTime()));
    event.setEndTime(Optional.ofNullable(endTime).orElse(updateDto.getEndTime()));
    event.setUpdatedAt(Instant.now());
    event.setMap(Optional.ofNullable(map).orElse(updateDto.getMap()));
    return event;
  }
}
