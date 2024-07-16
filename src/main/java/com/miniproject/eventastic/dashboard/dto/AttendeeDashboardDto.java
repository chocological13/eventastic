package com.miniproject.eventastic.dashboard.dto;

import com.miniproject.eventastic.attendee.entity.Attendee;
import java.time.LocalDate;
import lombok.Data;

@Data
public class AttendeeDashboardDto {
  private Long userId;
  private String userName;
  private String eventTitle;
  private LocalDate attendingAt;
  private Integer ticketsPurchased;

  public AttendeeDashboardDto(Attendee attendee) {
    this.userId = attendee.getUser().getId();
    this.userName = attendee.getUser().getUsername();
    this.eventTitle = attendee.getEvent().getTitle();
    this.attendingAt = attendee.getAttendingAt();
    this.ticketsPurchased = attendee.getTicketsPurchased();
  }

  public AttendeeDashboardDto toDto(Attendee attendee) {
    return new AttendeeDashboardDto(attendee);
  }
}
