package com.sysco.perso.analytics.service;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;

import java.util.List;

public interface PromoCodesService {

  PromoCode createInitialPerksPromoCode(CustomerInfo customerInfo);

  PromoCode createDTCPromoCode(OfferFulfillmentEvent event);

  List<PromoCode> createBatchPerksPromoCodes(List<Enrollment> enrollment);

  void processPromoCodesChanges(List<PromoCode> promoCodes);
}
