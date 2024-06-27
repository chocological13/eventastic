package com.miniproject.eventastic.image.entity;

import com.miniproject.eventastic.users.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "image")
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_id_gen")
  @SequenceGenerator(name = "image_id_gen", sequenceName = "image_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "image_name", nullable = false, length = Integer.MAX_VALUE)
  private String imageName;

  @NotNull
  @Column(name = "image_url", nullable = false, length = Integer.MAX_VALUE)
  private String imageUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private Users owner;

}