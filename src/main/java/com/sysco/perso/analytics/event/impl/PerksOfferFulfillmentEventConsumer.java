package com.sysco.perso.analytics.event.impl;

import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.event.EventConsumer;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import com.sysco.perso.analytics.event.util.JsonUtil;
import com.sysco.perso.analytics.service.OfferFulfillmentEventInfoService;
import com.sysco.perso.analytics.service.PerksFacade;
import com.sysco.perso.analytics.service.PromoCodesService;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PerksOfferFulfillmentEventConsumer implements EventConsumer {

  private final Logger logger = LoggerFactory.getLogger(PerksOfferFulfillmentEventConsumer.class);
  private static final String OFFER_FULFILLMENT_TOPIC = "dtc.fct.promotional-offer-fulfillment.0";
  private final PerksFacade perksFacade;

  private final PromoCodesService promoCodesService;

  private final OfferFulfillmentEventInfoService offerFulfillmentEventInfoService;

  @Autowired
  public PerksOfferFulfillmentEventConsumer(final PerksFacade perksFacade, final PromoCodesService promoCodesService,
                                            final OfferFulfillmentEventInfoService offerFulfillmentEventInfoService) {
    this.perksFacade = perksFacade;
    this.promoCodesService = promoCodesService;
    this.offerFulfillmentEventInfoService = offerFulfillmentEventInfoService;
  }

  @KafkaListener(id = "dtc-offer-fulfillment-listener", topics = OFFER_FULFILLMENT_TOPIC,
          containerFactory = "offerFulfillmentKafkaListenerContainerFactory")
  public void accept(final ConsumerRecord<String, GenericRecord> consumerRecord) {
    final OfferFulfillmentEvent event = JsonUtil.parseOfferFulfillmentEvent(consumerRecord.value());
    logger.info("PerksRewardFulfillmentEvent received -> {}", consumerRecord.value());

    PromoCode createdPromoCode = null;
    if (Objects.nonNull(event)) {
      if ("dtc".equals(event.getRewardSource()) && event.isSuccess()) {
        createdPromoCode = this.promoCodesService.createDTCPromoCode(event);
      } else if ("pfw".equals(event.getRewardSource())) {
        perksFacade.updateMembership(event);
      }
      this.offerFulfillmentEventInfoService.saveOfferFulfillmentEvents(event, createdPromoCode);
    }
  }

}
