package com.sysco.perso.analytics.entity;

import com.sysco.perso.analytics.entity.enums.EnrollmentType;
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
import java.time.OffsetDateTime;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"enrollment"})
@Table(name = "enrollment_history")
public class EnrollmentHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enrollment_history_sequence_generator")
  @SequenceGenerator(name = "enrollment_history_sequence_generator", sequenceName = "enrollment_history_sequence",
          allocationSize = 1)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @ManyToOne(targetEntity = Enrollment.class)
  @JoinColumn(name = "enrollment_id")
  private Enrollment enrollment;

  @Column(name = "enrollment_type", nullable = false, length = 32, unique = true)
  @Enumerated(EnumType.STRING)
  private EnrollmentType enrollmentType;

  @Column(name = "created_date", nullable = false, length = 32)
  private OffsetDateTime createdDate;

  @Column(name = "raw_payload", length = 65535)
  private String rawPayload;

  @Column(name = "is_fee_waive", nullable = false)
  private Boolean isFeeWaive;
}
