package com.sysco.perso.analytics.entity;

import com.sysco.perso.analytics.entity.enums.PromoCodeStatus;
import com.sysco.perso.analytics.entity.enums.ModifiedSource;
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
@EqualsAndHashCode(exclude = {"promoCode"})
@Table(name = "promo_code_history")
public class PromoCodeHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promo_code_history_sequence_generator")
  @SequenceGenerator(name = "promo_code_history_sequence_generator", sequenceName = "promo_code_history_sequence",
          allocationSize = 1)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @ManyToOne(targetEntity = PromoCode.class)
  @JoinColumn(name = "uuid")
  private PromoCode promoCode;

  @Column(name = "status", nullable = false, length = 10)
  @Enumerated(EnumType.STRING)
  private PromoCodeStatus promoCodeStatus;

  @Column(name = "created_date", nullable = false)
  private OffsetDateTime date;

  @Column(name = "modified_source", nullable = false, length = 10)
  @Enumerated(EnumType.STRING)
  private ModifiedSource modifiedSource;
}
