package com.miniproject.eventastic.event.metadata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "category", schema = "public")
public class Category {

  @Id
  @ColumnDefault("nextval('category_id_seq'::regclass)")
  @Column(name = "id", nullable = false)
  private Long id;

  @Size(max = 50)
  @Column(name = "name", length = 50)
  private String name;

  @Column(name = "image", length = Integer.MAX_VALUE)
  private String image;

}