package com.miniproject.eventastic.organizerWalletTrx.entity;

import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.trx.entity.Trx;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "organizer_wallet_trx", schema = "public", indexes = {
    @Index(name = "idx_organizer_wallet_trx_organizer_wallet_id", columnList = "organizer_wallet_id"),
    @Index(name = "idx_organizer_wallet_trx_trx_id", columnList = "trx_id")
})
public class OrganizerWalletTrx implements Comparable<OrganizerWalletTrx> {

  @Override
  public int compareTo(OrganizerWalletTrx o) {
    return o.getCreatedAt().compareTo(this.getCreatedAt());
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizer_wallet_trx_id_gen")
  @SequenceGenerator(name = "organizer_wallet_trx_id_gen", sequenceName = "organizer_wallet_trx_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "organizer_wallet_id", nullable = false)
  private OrganizerWallet organizerWallet;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "trx_id")
  private Trx trx;

  @NotNull
  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "trx_type", length = Integer.MAX_VALUE)
  private String trxType;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "service_fee")
  private BigDecimal serviceFee;

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }

}