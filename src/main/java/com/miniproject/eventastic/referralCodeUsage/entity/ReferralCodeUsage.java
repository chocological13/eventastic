package com.miniproject.eventastic.referralCodeUsage.entity;

import com.miniproject.eventastic.referralCodeUsage.entity.composite.ReferralCodeUsageId;
import com.miniproject.eventastic.users.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "referral_code_usage", schema = "public")
public class ReferralCodeUsage {

  @EmbeddedId
  private ReferralCodeUsageId id;

  @MapsId("usedById")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "used_by_id", nullable = false)
  private Users usedBy;

  @MapsId("codeOwnerId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "code_owner_id", nullable = false)
  private Users codeOwner;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "used_at")
  private Instant usedAt;

  @PrePersist
  protected void onCreate() {
    this.usedAt = Instant.now();
  }

}