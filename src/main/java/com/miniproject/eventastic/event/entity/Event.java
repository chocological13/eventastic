package com.miniproject.eventastic.event.entity;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.event.metadata.Category;
import com.miniproject.eventastic.image.entity.ImageEvent;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.entity.VoucherUsage;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
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
    @Index(name = "idx_event_organizer_id", columnList = "organizer_id")
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @NotNull
  @Column(name = "location", nullable = false, length = Integer.MAX_VALUE)
  private String location;

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

  @Column(name = "seat_availability")
  private Integer seatAvailability;

  @NotNull
  @Column(name = "is_free", nullable = false, columnDefinition = "false")
  private Boolean isFree;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at")
  private Instant createdAt;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_image_id")
  private ImageEvent eventImage;

  @Column(name = "map", length = Integer.MAX_VALUE)
  private String map;

  @NotNull
  @Column(name = "referral_voucher_usage_limit", nullable = false)
  private Integer referralVoucherUsageLimit;

  @Column(name = "referral_voucher_usage_availability")
  private Integer referralVoucherUsageAvailability;

  @Column(name = "promo_percent")
  private Integer promoPercent;

  @Column(name = "promo_end_date")
  private Instant promoEndDate;

  @OneToMany(mappedBy = "event")
  private Set<Review> reviews = new LinkedHashSet<>();

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<TicketType> ticketTypes = new LinkedHashSet<>();

  @OneToMany(mappedBy = "event")
  private Set<Trx> trxes = new LinkedHashSet<>();

  @OneToMany(mappedBy = "event")
  private Set<Voucher> vouchers = new LinkedHashSet<>();

  @OneToMany(mappedBy = "event")
  private Set<Attendee> attendees = new LinkedHashSet<>();

  @OneToMany(mappedBy = "event")
  private Set<Ticket> tickets = new LinkedHashSet<>();

  @OneToMany(mappedBy = "event")
  private Set<VoucherUsage> voucherUsages = new LinkedHashSet<>();


  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }
}