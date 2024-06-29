package com.miniproject.eventastic.toHandle;

import com.miniproject.eventastic.users.entity.Users;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "points_wallet", schema = "public", indexes = {
    @Index(name = "idx_points_wallet_user_id", columnList = "user_id"),
    @Index(name = "idx_user_id", columnList = "user_id")
})
public class PointsWallet {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "points_wallet_id_gen")
  @SequenceGenerator(name = "points_wallet_id_gen", sequenceName = "points_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private Users user;

  @Column(name = "points")
  private BigDecimal points;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "awarded_at")
  private Instant awardedAt;

  @ColumnDefault("(CURRENT_TIMESTAMP + '90 days'::interval)")
  @Column(name = "expires_at")
  private Instant expiresAt;

  @NotNull
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @OneToMany(mappedBy = "pointsWallet")
  private Set<PointsTrx> pointsTrxes = new LinkedHashSet<>();

  @OneToMany(mappedBy = "points")
  private Set<Trx> trxes = new LinkedHashSet<>();

}