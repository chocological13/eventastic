package com.miniproject.eventastic.voucher.entity;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.users.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "voucher", schema = "public")
public class Voucher {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "voucher_id_gen")
  @SequenceGenerator(name = "voucher_id_gen", sequenceName = "voucher_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Size(max = 10)
  @NotNull
  @Column(name = "code", nullable = false, length = 10)
  private String code;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "awardee_id")
  private Users awardee;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  private Event event;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organizer_id")
  private Users organizer;

  @Column(name = "use_limit")
  private Integer useLimit;

}