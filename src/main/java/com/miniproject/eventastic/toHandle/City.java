package com.miniproject.eventastic.toHandle;

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
@Table(name = "city", schema = "public", indexes = {
    @Index(name = "idx_city_province_id", columnList = "province_id")
})
public class City {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city_id_gen")
  @SequenceGenerator(name = "city_id_gen", sequenceName = "city_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "city_name", nullable = false, length = Integer.MAX_VALUE)
  private String cityName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "province_id")
  private Province province;

  @OneToMany(mappedBy = "city")
  private Set<District> districts = new LinkedHashSet<>();

}