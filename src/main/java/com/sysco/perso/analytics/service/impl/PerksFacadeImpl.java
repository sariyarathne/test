package com.sysco.perso.analytics.service.impl;

import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import com.sysco.perso.analytics.service.CRMService;
import com.sysco.perso.analytics.service.EnrollmentService;
import com.sysco.perso.analytics.service.OfferTrackerService;
import com.sysco.perso.analytics.service.PerksFacade;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class PerksFacadeImpl implements PerksFacade {

  private final EnrollmentService enrollmentService;

  private final CRMService crmService;

  private final OfferTrackerService offerTrackerService;

  public PerksFacadeImpl(final EnrollmentService enrollmentService, final CRMService crmService,
                         final OfferTrackerService offerTrackerService) {
    this.enrollmentService = enrollmentService;
    this.crmService = crmService;
    this.offerTrackerService = offerTrackerService;
  }

  @Override
  public void enroll(final AccountAttributeChangeEvent event) {
    final Enrollment enrollment = this.enrollmentService.save(event);
    this.crmService.processMembershipFee(Collections.singletonList(enrollment));
  }

  @Override
  public void updateMembership(final OfferFulfillmentEvent event) {
    final Enrollment enrollment = this.enrollmentService.save(event);
    if (Objects.nonNull(enrollment)) {
      this.crmService.processMembershipFee(Collections.singletonList(enrollment));
    }
  }

  @Override
  public void processBatch(List<Enrollment> enrollments) {
    this.offerTrackerService.processEnrollmentChanges(enrollments);
    this.crmService.processMembershipFee(Collections.unmodifiableList(enrollments));
  }
}
