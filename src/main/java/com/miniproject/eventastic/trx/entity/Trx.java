package com.miniproject.eventastic.trx.entity;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.ticket.entity.Ticket;
import com.miniproject.eventastic.ticketType.entity.TicketType;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.voucher.entity.Voucher;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
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
@Table(name = "trx", schema = "public", indexes = {
    @Index(name = "idx_trx_user_id", columnList = "user_id"),
    @Index(name = "idx_trx_event_id", columnList = "event_id"),
    @Index(name = "idx_trx_points_id", columnList = "points_id"),
    @Index(name = "idx_trx_voucher_id", columnList = "voucher_id")
})
public class Trx {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trx_id_gen")
  @SequenceGenerator(name = "trx_id_gen", sequenceName = "trx_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private Users user;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  @NotNull
  @ColumnDefault("1")
  @Min(value = 1)
  @Column(name = "qty", nullable = false)
  private Integer qty;

  @Column(name = "initial_amount")
  private BigDecimal initialAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "points_id")
  private PointsWallet pointsWallet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "voucher_id")
  private Voucher voucher;

  @Column(name = "total_amount")
  private BigDecimal totalAmount;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "trx_date")
  private Instant trxDate;

  @ColumnDefault("false")
  @Column(name = "is_canceled")
  private Boolean isCanceled;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id")
  private Payment payment;

  @NotNull
  @ColumnDefault("false")
  @Column(name = "is_paid", nullable = false)
  private Boolean isPaid = false;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ticket_type_id", nullable = false)
  private TicketType ticketType;

  @OneToMany(mappedBy = "trx", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<Ticket> tickets = new LinkedHashSet<>();

  @PrePersist
  protected void onCreate() {
    this.trxDate = Instant.now();
  }

}