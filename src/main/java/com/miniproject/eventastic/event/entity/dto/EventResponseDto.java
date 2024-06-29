package com.miniproject.eventastic.event.entity.dto;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.ticketType.entity.dto.TicketTypeDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventResponseDto {

  @NotNull
  private String organizerName;

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
  private boolean isFree;

  @NotEmpty
  private Set<TicketTypeDto> ticketTypes;


  public EventResponseDto toDto(Event event, Set<TicketTypeDto> ticketTypesDto) {
    EventResponseDto eventResponseDto = new EventResponseDto();
    eventResponseDto.setOrganizerName(event.getOrganizer().getUsername());
    eventResponseDto.setTitle(event.getTitle());
    eventResponseDto.setDescription(event.getDescription());
    eventResponseDto.setLocation(event.getLocation());
    eventResponseDto.setVenue(event.getVenue());
    eventResponseDto.setEventDate(event.getEventDate());
    eventResponseDto.setStartTime(event.getStartTime());
    eventResponseDto.setEndTime(event.getEndTime());
    eventResponseDto.setEventCategory(event.getEventCategory());
    eventResponseDto.setFree(event.getIsFree());
    eventResponseDto.setTicketTypes(ticketTypesDto);
    return eventResponseDto;
  }

}
