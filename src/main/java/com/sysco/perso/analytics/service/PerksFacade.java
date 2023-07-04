package com.sysco.perso.analytics.service;

import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;

import java.util.List;

public interface PerksFacade {

  void enroll(AccountAttributeChangeEvent event);

  void updateMembership(OfferFulfillmentEvent event);

  void processBatch(List<Enrollment> enrollments);
}
