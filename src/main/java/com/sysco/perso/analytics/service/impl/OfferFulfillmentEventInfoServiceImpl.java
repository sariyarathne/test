package com.sysco.perso.analytics.service.impl;

import com.sysco.perso.analytics.entity.OfferFulfillmentEventInfo;
import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import com.sysco.perso.analytics.repository.OfferFulfillmentEventInfoRepository;
import com.sysco.perso.analytics.service.OfferFulfillmentEventInfoService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class OfferFulfillmentEventInfoServiceImpl implements OfferFulfillmentEventInfoService {

  private final OfferFulfillmentEventInfoRepository offerFulfillmentEventsInfoRepository;

  public OfferFulfillmentEventInfoServiceImpl(final OfferFulfillmentEventInfoRepository offerFulfillmentEventsInfoRepository) {
    this.offerFulfillmentEventsInfoRepository = offerFulfillmentEventsInfoRepository;
  }

  @Override
  public OfferFulfillmentEventInfo saveOfferFulfillmentEvents(OfferFulfillmentEvent event, PromoCode promoCode) {

    return offerFulfillmentEventsInfoRepository.save(
            OfferFulfillmentEventInfo.builder().offerId(event.getOfferId()).campaignId(event.getCampaignId())
                    .campaignName(event.getCampaignName())
                    .customerNumber(event.getCustomerNumber()).opcoNumber(event.getOpcoNumber()).rewardSource(
                            event.getRewardSource()).offerName(event.getOfferName()).rewardValue(event.getRewardValue())
                    .offerDescription(event.getOfferDescription()).offerEndDate(event.getOfferEndDate())
                    .isSuccess(event.isSuccess()).promoCode(promoCode).build());

  }

}
