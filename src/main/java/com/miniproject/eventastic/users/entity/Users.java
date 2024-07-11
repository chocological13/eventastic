package com.miniproject.eventastic.users.entity;

import com.miniproject.eventastic.attendee.entity.Attendee;
import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.image.entity.ImageUserAvatar;
import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.review.entity.Review;
import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.trx.entity.Trx;
import com.miniproject.eventastic.voucher.entity.Voucher;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
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
@Table(name = "users")
public class Users {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "users_id_gen")
  @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "username", nullable = false, length = 20)
  private String username;

  @NotNull
  @Column(name = "email", nullable = false, length = 50)
  private String email;

  @NotNull
  @Column(name = "password", nullable = false)
  private String password;

  @ColumnDefault("false")
  @Column(name = "is_organizer")
  private Boolean isOrganizer = false;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at")
  private Instant createdAt;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "avatar_id")
  private ImageUserAvatar avatar;

  @Size(max = 50)
  @Column(name = "full_name", length = 50)
  private String fullName;

  @Column(name = "bio", length = Integer.MAX_VALUE)
  private String bio;

  @Column(name = "birthday")
  private Date birthday;

  @Size(max = 7)
  @Column(name = "ref_code_used", length = 7)
  private String refCodeUsed;

  @Size(max = 7)
  @Column(name = "owned_ref_code", length = 7)
  private String ownedRefCode;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private PointsWallet pointsWallet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organizer_wallet_id")
  private OrganizerWallet organizerWallet;

  @OneToMany(mappedBy = "user")
  private Set<Attendee> attendees = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user")
  private Set<ImageUserAvatar> imageUserAvatars = new LinkedHashSet<>();

  @OneToMany(mappedBy = "reviewer")
  private Set<Review> reviews = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user")
  private Set<Ticket> tickets = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user")
  private Set<Trx> trxes = new LinkedHashSet<>();

  @OneToMany(mappedBy = "awardee")
  private Set<Voucher> vouchers = new LinkedHashSet<>();

  @OneToMany(mappedBy = "organizer")
  private Set<Event> events = new LinkedHashSet<>();


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