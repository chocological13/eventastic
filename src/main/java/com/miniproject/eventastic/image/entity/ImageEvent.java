package com.miniproject.eventastic.image.entity;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "image_event", schema = "public", indexes = {
    @Index(name = "idx_image_event_event_id", columnList = "event_id")
})
public class ImageEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_event_id_gen")
  @SequenceGenerator(name = "image_event_id_gen", sequenceName = "image_event_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "organizer_id", nullable = false)
  private Users organizer;

  @Column(name = "image_name", length = Integer.MAX_VALUE)
  private String imageName;

  @Column(name = "image_url", length = Integer.MAX_VALUE)
  private String imageUrl;


}