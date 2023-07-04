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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode(exclude = {"enrollmentHistorySet", "perksTrackingDataSet"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "enrollment")
public class Enrollment {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enrollment_sequence_generator")
  @SequenceGenerator(name = "enrollment_sequence_generator", sequenceName = "enrollment_sequence", initialValue = 1,
          allocationSize = 1)
  @Column(name = "enrollment_id", updatable = false, nullable = false)
  private Long id;

  @OneToOne(targetEntity = CustomerInfo.class,
          cascade = {javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.MERGE})
  @JoinColumn(name = "customer_id")
  private CustomerInfo customerInfo;

  @OneToMany(targetEntity = EnrollmentHistory.class)
  @JoinColumn(name = "enrollment_id")
  private final Set<EnrollmentHistory> enrollmentHistorySet = new HashSet<>();

  @OneToMany(targetEntity = PerksTrackingData.class)
  @JoinColumn(name = "enrollment_id")
  private final Set<PerksTrackingData> perksTrackingDataSet = new HashSet<>();

  @Column(name = "last_modified_date", nullable = false, length = 45)
  private OffsetDateTime lastModifiedDate;

  @Column(name = "enrollment", nullable = false, length = 45)
  @Enumerated(EnumType.STRING)
  private EnrollmentType enrollmentType;

  @Column(name = "raw_payload", length = 65535)
  private String rawPayload;

  @Column(name = "free_trial_end_date", nullable = false, length = 45)
  private LocalDate freeTrialEndDate;

  @Column(name = "fee_waiver_end_date", nullable = false, length = 45)
  private OffsetDateTime feeWaiverEndDate;

  @Column(name = "is_trial_completed", nullable = false)
  private Boolean isTrialCompleted;

  @Column(name = "is_fee_waive", nullable = false)
  private Boolean isFeeWaive;

}
