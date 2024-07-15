package com.miniproject.eventastic.voucher.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class VoucherUsageId implements Serializable {

  @NotNull
  @Column(name = "user_id", nullable = false)
  private Long userId;

  @NotNull
  @Column(name = "voucher_id", nullable = false)
  private Long voucherId;

  @NotNull
  @Column(name = "event_id", nullable = false)
  private Long eventId;

}