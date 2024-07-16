package com.miniproject.eventastic.dashboard.dto;

import com.miniproject.eventastic.attendee.entity.Attendee;
import java.time.LocalDate;
import lombok.Data;

@Data
public class AttendeeDto {
  private Long userId;
  private String userName;
  private String eventTitle;
  private LocalDate attendingAt;
  private Integer ticketsPurchased;

  public AttendeeDto(Attendee attendee) {
    this.userId = attendee.getUser().getId();
    this.userName = attendee.getUser().getUsername();
    this.eventTitle = attendee.getEvent().getTitle();
    this.attendingAt = attendee.getAttendingAt();
    this.ticketsPurchased = attendee.getTicketsPurchased();
  }

  public AttendeeDto toDto(Attendee attendee) {
    return new AttendeeDto(attendee);
  }
}
