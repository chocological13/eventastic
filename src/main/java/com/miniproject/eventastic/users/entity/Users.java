package com.miniproject.eventastic.users.entity;

import com.miniproject.eventastic.referralCode.entity.ReferralCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Users {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "users_id_gen")
  @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @Size(max = 20)
  @NotNull
  @Column(name = "username", nullable = false, length = 20)
  private String username;

  @Size(max = 50)
  @NotNull
  @Column(name = "email", nullable = false, length = 50)
  private String email;

  @Size(min = 8)
  @NotNull
  @Column(name = "password", nullable = false)
  private String password;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(unique = true, name = "ref_code_used_id")
  private ReferralCode referralCode;

  @ColumnDefault("false")
  @Column(name = "is_organizer")
  private Boolean isOrganizer;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at")
  private Instant createdAt;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @Column(name = "avatar", length = Integer.MAX_VALUE)
  private String avatar;

  @Size(max = 50)
  @Column(name = "first_name", length = 50)
  private String firstName;

  @Size(max = 50)
  @Column(name = "last_name", length = 50)
  private String lastName;

  @Column(name = "bio", length = Integer.MAX_VALUE)
  private String bio;

  @Column(name = "birthday")
  private Date birthday;

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }
}