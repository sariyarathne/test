package com.sysco.perso.analytics.entity;

import com.sysco.perso.analytics.validators.ValidCustomerNumberFormat;
import com.sysco.perso.analytics.validators.ValidOpcoIdFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "customer_info")
public class CustomerInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_sequence_generator")
  @SequenceGenerator(name = "customer_sequence_generator", sequenceName = "customer_sequence", initialValue = 1,
          allocationSize = 1)
  @Column(name = "customer_info_id", updatable = false, nullable = false)
  private Long id;

  @ValidCustomerNumberFormat
  @Column(name = "customer_id", nullable = false, length = 10, unique = true)
  private String customerId;

  @ValidOpcoIdFormat
  @Column(name = "opco_id", nullable = false, length = 32, unique = true)
  private String opcoId;
}