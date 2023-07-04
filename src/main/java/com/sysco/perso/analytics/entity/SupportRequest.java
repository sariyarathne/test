package com.sysco.perso.analytics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;


@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "support_request")
public class SupportRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "support_request_sequence_generator")
  @SequenceGenerator(name = "support_request_sequence_generator", sequenceName = "support_request_sequence", initialValue = 1,
          allocationSize = 1)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @ManyToOne(targetEntity = Enrollment.class)
  @JoinColumn(name = "enrollment_id")
  private Enrollment enrollment;

  @Column(name = "sr_reference", nullable = false, length = 32)
  private String srReference;

  @Column(name = "sr_created_date", nullable = false)
  private LocalDate srCreatedDate;
}
