package com.miniproject.eventastic.attendee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeId implements Serializable {
  private Long userId;
  private Long eventId;

}