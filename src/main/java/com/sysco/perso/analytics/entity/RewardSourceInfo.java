package com.sysco.perso.analytics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode(exclude = {"promoCodeSet"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reward_source_info")
public class RewardSourceInfo {

  @Id
  @Column(name = "reward_source_id", updatable = false, nullable = false)
  private String rewardSourceId;

  @Column(name = "reward_source", nullable = false, length = 32)
  private String rewardSource;

  @OneToMany(targetEntity = PromoCode.class)
  @JoinColumn(name = "reward_source_id")
  private final Set<PromoCode> promoCodeSet = new HashSet<>();
}
