package com.sysco.perso.analytics.repository;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.entity.enums.PromoCodeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, String> {
  Page<PromoCode> findAllByPromoCodeStatusNotIn(Collection<PromoCodeStatus> promoCodeStatuses, Pageable pageable);
  List<PromoCode> findByCode(String code);
}