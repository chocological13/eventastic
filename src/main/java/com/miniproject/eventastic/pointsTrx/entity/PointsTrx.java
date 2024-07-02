package com.miniproject.eventastic.pointsTrx.entity;

import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.toHandle.Trx;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "points_trx", schema = "public", indexes = {
    @Index(name = "idx_points_trx_points_wallet_id", columnList = "points_wallet_id"),
    @Index(name = "idx_points_trx_trx_id", columnList = "trx_id")
})
public class PointsTrx {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "points_trx_id_gen")
  @SequenceGenerator(name = "points_trx_id_gen", sequenceName = "points_trx_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "points_wallet_id", nullable = false)
  private PointsWallet pointsWallet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trx_id")
  private Trx trx;

  @Column(name = "points")
  private BigDecimal points;

  @Column(name = "description", length = Integer.MAX_VALUE)
  private String description;
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at")
  private Instant createdAt;

/*
 TODO [Reverse Engineering] create field to map the 'points_trx_type' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "points_trx_type", columnDefinition = "points_trx_type not null")
    private Object pointsTrxType;
*/
}