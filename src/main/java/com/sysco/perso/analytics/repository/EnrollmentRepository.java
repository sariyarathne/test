package com.sysco.perso.analytics.repository;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
  List<Enrollment> findByCustomerInfo(CustomerInfo customerInfo);

  Page<Enrollment> findAllByEnrollmentTypeIn(Collection<EnrollmentType> enrollmentTypes, Pageable pageable);
}
