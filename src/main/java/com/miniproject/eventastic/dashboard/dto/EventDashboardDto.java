package com.miniproject.eventastic.dashboard.dto;

import com.miniproject.eventastic.event.entity.Event;
import java.time.LocalDate;
import lombok.Data;

@Data
public class EventDashboardDto {
  private Long id;
  private String title;
  private LocalDate eventDate;
  private String location;
  private String venue;
  private Integer seatAvailability;
  private Boolean isFree;

  public EventDashboardDto(Event event) {
    this.id = event.getId();
    this.title = event.getTitle();
    this.eventDate = event.getEventDate();
    this.location = event.getLocation();
    this.venue = event.getVenue();
    this.seatAvailability = event.getSeatAvailability();
    this.isFree = event.getIsFree();
  }

  public EventDashboardDto toDto(Event event) {
    return new EventDashboardDto(event);
  }
}
