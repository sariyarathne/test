package com.sysco.perso.analytics.service;

import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;

public interface EnrollmentService {
  Enrollment save(AccountAttributeChangeEvent event);

  Enrollment save(OfferFulfillmentEvent event);
}
