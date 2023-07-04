package com.sysco.perso.analytics.repository;

import com.sysco.perso.analytics.entity.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {

  List<SupportRequest> findBySrCreatedDateBetween(LocalDate date1, LocalDate date2);
}
