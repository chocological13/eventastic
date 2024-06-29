package com.miniproject.eventastic.toHandle;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "payment", schema = "public", indexes = {
    @Index(name = "idx_payment_trx_id", columnList = "trx_id")
})
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_id_gen")
  @SequenceGenerator(name = "payment_id_gen", sequenceName = "payment_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "trx_id", nullable = false)
  private Trx trx;

  @NotNull
  @Column(name = "payment_method", nullable = false, length = Integer.MAX_VALUE)
  private String paymentMethod;

  @NotNull
  @ColumnDefault("'Pending'")
  @Column(name = "status", nullable = false, length = Integer.MAX_VALUE)
  private String status;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "verification_code", length = Integer.MAX_VALUE)
  private String verificationCode;

  @Column(name = "verified_at")
  private Instant verifiedAt;

  @NotNull
  @ColumnDefault("false")
  @Column(name = "is_verified", nullable = false)
  private Boolean isVerified = false;

  @OneToMany(mappedBy = "payment")
  private Set<OrganizerPayout> organizerPayouts = new LinkedHashSet<>();

  @OneToMany(mappedBy = "payment")
  private Set<Trx> trxes = new LinkedHashSet<>();

}