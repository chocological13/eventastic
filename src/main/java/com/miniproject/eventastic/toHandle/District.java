package com.miniproject.eventastic.toHandle;

import com.miniproject.eventastic.event.entity.Event;
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
@Table(name = "district", schema = "public", indexes = {
    @Index(name = "idx_district_city_id", columnList = "city_id")
})
public class District {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "district_id_gen")
  @SequenceGenerator(name = "district_id_gen", sequenceName = "district_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "district_name", nullable = false, length = Integer.MAX_VALUE)
  private String districtName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "city_id")
  private City city;

  @OneToMany(mappedBy = "location")
  private Set<Event> events = new LinkedHashSet<>();

}