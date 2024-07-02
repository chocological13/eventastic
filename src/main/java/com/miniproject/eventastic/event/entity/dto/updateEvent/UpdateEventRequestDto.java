package com.miniproject.eventastic.event.entity.dto.updateEvent;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.ticketType.entity.dto.TicketTypeRequestDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
  private Event.EventCategory eventCategory;
  private String location;
  private String venue;
  private LocalDate eventDate;
  private LocalTime startTime;
  private LocalTime endTime;

  public Event dtoToEvent(Event existingEvent, UpdateEventRequestDto updateDto) {
    existingEvent.setTitle(Optional.ofNullable(title).orElse(updateDto.getTitle()));
    existingEvent.setDescription(Optional.ofNullable(description).orElse(updateDto.getDescription()));
    existingEvent.setEventCategory(Optional.ofNullable(eventCategory).orElse(updateDto.getEventCategory()));
    existingEvent.setLocation(Optional.ofNullable(location).orElse(updateDto.getLocation()));
    existingEvent.setVenue(Optional.ofNullable(venue).orElse(updateDto.getVenue()));
    existingEvent.setEventDate(Optional.ofNullable(eventDate).orElse(updateDto.getEventDate()));
    existingEvent.setStartTime(Optional.ofNullable(startTime).orElse(updateDto.getStartTime()));
    existingEvent.setEndTime(Optional.ofNullable(endTime).orElse(updateDto.getEndTime()));
    return existingEvent;
  }
}
