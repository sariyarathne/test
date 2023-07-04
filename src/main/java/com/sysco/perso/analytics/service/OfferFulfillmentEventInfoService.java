package com.sysco.perso.analytics.service;

import com.sysco.perso.analytics.entity.OfferFulfillmentEventInfo;
import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;

public interface OfferFulfillmentEventInfoService {
  OfferFulfillmentEventInfo saveOfferFulfillmentEvents(OfferFulfillmentEvent offerFulfillmentEvent, PromoCode promoCode);
}
