package com.miniproject.eventastic.event.entity;

import com.miniproject.eventastic.toHandle.Review;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.toHandle.Trx;
import com.miniproject.eventastic.users.entity.Users;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event", schema = "public", indexes = {
    @Index(name = "idx_event_organizer_id", columnList = "organizer_id"),
    @Index(name = "idx_event_location_id", columnList = "location_id")
})
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_id_gen")
  @SequenceGenerator(name = "event_id_gen", sequenceName = "event_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organizer_id")
  private Users organizer;

  @NotNull
  @Column(name = "title", nullable = false, length = Integer.MAX_VALUE)
  private String title;

  @NotNull
  @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
  private String description;

  @NotNull
  @Column(name = "location", nullable = false, length = Integer.MAX_VALUE)
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_category", nullable = false)
  private EventCategory eventCategory;

  @NotNull
  @Column(name = "venue", nullable = false, length = Integer.MAX_VALUE)
  private String venue;

  @NotNull
  @Column(name = "event_date", nullable = false)
  private LocalDate eventDate;

  @NotNull
  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @NotNull
  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Column(name = "seat_limit")
  private Integer seatLimit;

  @Column(name = "available_seat")
  private Integer availableSeat;

  @NotNull
  @ColumnDefault("false")
  @Column(name = "is_free", nullable = false)
  private Boolean isFree = false;

  @OneToMany(mappedBy = "event")
  private Set<Review> reviews = new LinkedHashSet<>();

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<TicketType> ticketTypes = new LinkedHashSet<>();

  @OneToMany(mappedBy = "event")
  private Set<Trx> trxes = new LinkedHashSet<>();

  // enum
  public enum EventCategory {
    ONLINE,
    CONCERT,
    CONFERENCE,
    WORKSHOP,
    FESTIVAL,
    SPORTING_EVENT,
    ART_EXHIBITION,
    FOOD_BEVERAGE,
    BUSINESS_MEETING,
    SEMINAR,
    OTHER
  }
}