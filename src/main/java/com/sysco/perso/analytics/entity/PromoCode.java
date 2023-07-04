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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"rewardSourceInfo"})
@Table(name = "promo_code")
public class PromoCode {

  @Id
  @Column(name = "uuid", updatable = false, length = 50)
  private String uuid;

  @Column(name = "code", nullable = false, length = 32)
  private String code;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "description", nullable = false, length = 100)
  private String description;

  @Column(name = "reward", nullable = false)
  private double reward;

  @Column(name = "reward_currency", nullable = false, length = 5)
  private String rewardCurrency;

  @Column(name = "minimum_bucket_size", nullable = false)
  private double minimumBucketSize;

  @Column(name = "status", nullable = false, length = 10)
  @Enumerated(EnumType.STRING)
  private PromoCodeStatus promoCodeStatus;

  @Column(name = "generated_date", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime generatedDate;

  @Column(name = "code_start_date", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime codeStartDate;

  @Column(name = "code_end_date", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime codeEndDate;

  @Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime lastModifiedDate;

  @Column(name = "modified_source", nullable = false, length = 10)
  @Enumerated(EnumType.STRING)
  private ModifiedSource modifiedSource;

  @ManyToOne(targetEntity = CustomerInfo.class)
  @JoinColumn(name = "customer_info_id", nullable = false)
  private CustomerInfo customerInfo;

  @ManyToOne(targetEntity = RewardSourceInfo.class)
  @JoinColumn(name = "reward_source_id", nullable = false)
  private RewardSourceInfo rewardSourceInfo;

  @OneToMany(targetEntity = PromoCodeHistory.class)
  @JoinColumn(name = "uuid")
  private final Set<PromoCodeHistory> promoCodeHistorySet = new HashSet<>();

}
