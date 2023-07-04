package com.sysco.perso.analytics.entity;

import com.sysco.perso.analytics.entity.enums.TrackingState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"enrollment"})
@Table(name = "perks_tracking_data")
public class PerksTrackingData {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perks_tracking_data_sequence_generator")
  @SequenceGenerator(name = "perks_tracking_data_sequence_generator", sequenceName = "perks_tracking_data_sequence",
          initialValue = 1,
          allocationSize = 1)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @ManyToOne(targetEntity = Enrollment.class)
  @JoinColumn(name = "enrollment_id")
  private Enrollment enrollment;

  @Column(name = "start_date", nullable = false, length = 32)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "tracking_state", nullable = false)
  @Enumerated(EnumType.STRING)
  private TrackingState trackingState;

  @Column(name = "created_date", nullable = false)
  private OffsetDateTime createdDate;
}
