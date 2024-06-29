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
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "voucher", schema = "public", indexes = {
    @Index(name = "idx_voucher_awarded_to", columnList = "awarded_to")
})
public class Voucher {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "voucher_id_gen")
  @SequenceGenerator(name = "voucher_id_gen", sequenceName = "vouchers_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "awarded_to")
  private Users awardedTo;

  @ColumnDefault("false")
  @Column(name = "is_for_all")
  private Boolean isForAll;

  @NotNull
  @Column(name = "discount_percentage", nullable = false)
  private Integer discountPercentage;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @NotNull
  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @OneToMany(mappedBy = "voucher")
  private Set<Trx> trxes = new LinkedHashSet<>();

}