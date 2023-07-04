package com.sysco.perso.analytics.repository;

import com.sysco.perso.analytics.entity.EnrollmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentHistoryRepository extends JpaRepository<EnrollmentHistory, Long> {

}