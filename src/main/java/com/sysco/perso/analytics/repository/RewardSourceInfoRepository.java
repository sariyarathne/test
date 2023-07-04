package com.sysco.perso.analytics.repository;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.RewardSourceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RewardSourceInfoRepository extends JpaRepository<RewardSourceInfo, String> {
  Optional<RewardSourceInfo> findByRewardSource(String rewardSource);
}
