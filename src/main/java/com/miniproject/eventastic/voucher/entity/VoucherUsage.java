package com.miniproject.eventastic.voucher.entity;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.users.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "voucher_usage", schema = "public", indexes = {
    @Index(name = "idx_voucher_usage_user_id", columnList = "user_id"),
    @Index(name = "idx_voucher_usage_voucher_id", columnList = "voucher_id")
})
public class VoucherUsage {

  @EmbeddedId
  private VoucherUsageId id;

  @MapsId("userId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private Users user;

  @MapsId("voucherId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "voucher_id", nullable = false)
  private Voucher voucher;

  @NotNull
  @Column(name = "used_at", nullable = false)
  private Instant usedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  private Event event;

}