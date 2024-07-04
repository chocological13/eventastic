package com.miniproject.eventastic.voucher.entity;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.users.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "voucher", schema = "public", indexes = {
    @Index(name = "idx_voucher_awardee_id", columnList = "awardee_id"),
    @Index(name = "idx_voucher_event_id", columnList = "event_id")
})
public class Voucher {

  @Id
  @Size(max = 10)
  @Column(name = "code", nullable = false, length = 10)
  private String code;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "awardee_id")
  private Users awardee;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  private Event event;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organizer_id")
  private Users organizer;

  @Column(name = "description", length = Integer.MAX_VALUE)
  private String description;

  @NotNull
  @Column(name = "percent_discount", nullable = false)
  private Integer percentDiscount;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @Column(name = "expires_at")
  private Instant expiresAt;

  @Column(name = "use_limit")
  private Integer useLimit;


  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }

  @PreUpdate
  protected void onUpdate() {
    createdAt = Instant.now();
  }

}