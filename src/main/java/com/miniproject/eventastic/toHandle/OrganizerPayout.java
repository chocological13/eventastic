package com.miniproject.eventastic.toHandle;

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
@Table(name = "organizer_payout", schema = "public", indexes = {
    @Index(name = "idx_organizer_payout_organizer_wallet_id", columnList = "organizer_wallet_id"),
    @Index(name = "idx_organizer_payout_payment_id", columnList = "payment_id")
})
public class OrganizerPayout {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizer_payout_id_gen")
  @SequenceGenerator(name = "organizer_payout_id_gen", sequenceName = "organizer_payout_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "organizer_wallet_id", nullable = false)
  private OrganizerWallet organizerWallet;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "trx_id", nullable = false)
  private Trx trx;

  @NotNull
  @Column(name = "amount", nullable = false, precision = 38, scale = 2)
  private BigDecimal amount;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "credited_at")
  private Instant creditedAt;

}