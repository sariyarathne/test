package com.sysco.perso.analytics.service;

import com.sysco.perso.analytics.entity.Enrollment;

import java.util.List;

public interface OfferTrackerService {
  void processEnrollmentChanges(List<Enrollment> enrollment);
}
