package com.sysco.perso.analytics.service;

import com.sysco.perso.analytics.entity.Enrollment;

import java.util.List;

public interface CRMService {
  void processMembershipFee(List<Enrollment> enrollments);
}
