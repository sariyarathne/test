package com.sysco.perso.analytics.repository;

import com.sysco.perso.analytics.entity.PromoCodeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromoCodeHistoryRepository extends JpaRepository<PromoCodeHistory, Long> {
}
