package com.miniproject.eventastic.toHandle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "province", schema = "public")
public class Province {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "province_id_gen")
  @SequenceGenerator(name = "province_id_gen", sequenceName = "province_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "province_name", nullable = false, length = Integer.MAX_VALUE)
  private String provinceName;

  @OneToMany(mappedBy = "province")
  private Set<City> cities = new LinkedHashSet<>();

}