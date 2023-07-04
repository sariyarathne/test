package com.sysco.perso.analytics.repository;

import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.PerksTrackingData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerksTrackingDataRepository extends JpaRepository<PerksTrackingData, Long> {
  List<PerksTrackingData>  findByEnrollmentOrderByCreatedDate(Enrollment enrollment);
}
