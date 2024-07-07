package com.miniproject.eventastic.organizerWallet.entity;

import com.miniproject.eventastic.toHandle.OrganizerPayout;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@Table(name = "organizer_wallet", schema = "public", indexes = {
    @Index(name = "idx_organizer_wallet_organizer_id", columnList = "organizer_id")
})
public class OrganizerWallet {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizer_wallet_id_gen")
  @SequenceGenerator(name = "organizer_wallet_id_gen", sequenceName = "organizer_wallet_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "organizer_id", nullable = false)
  private Users organizer;

  @Column(name = "balance")
  private BigDecimal balance;

  @NotNull
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @OneToMany(mappedBy = "organizerWallet")
  private Set<OrganizerPayout> organizerPayouts = new LinkedHashSet<>();

  @PrePersist
  protected void onCreate() {
    updatedAt = Instant.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }

}