package com.sysco.perso.analytics.event.Impl;

import com.sysco.perso.analytics.entity.OfferFulfillmentEventInfo;
import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.event.impl.PerksOfferFulfillmentEventConsumer;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import com.sysco.perso.analytics.service.OfferFulfillmentEventInfoService;
import com.sysco.perso.analytics.service.PerksFacade;
import com.sysco.perso.analytics.service.PromoCodesService;
import com.sysco.perso.analytics.testutils.AvroUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@EnableAutoConfiguration
@SpringBootTest
public class PerksOfferFulfillmentEventConsumerTest {

  @MockBean
  PerksFacade perksFacade;

  @Autowired
  PerksOfferFulfillmentEventConsumer perksOfferFulfillmentEventConsumer;

  @MockBean
  PromoCodesService promoCodesService;

  @MockBean
  OfferFulfillmentEventInfoService offerFulfillmentEventInfoService;

  @BeforeEach
  public void beforeEach() {
    Mockito.when(this.promoCodesService.createDTCPromoCode(ArgumentMatchers.any(OfferFulfillmentEvent.class)))
            .thenReturn(new PromoCode());
    Mockito.doNothing().when(perksFacade).updateMembership(ArgumentMatchers.any(OfferFulfillmentEvent.class));
  }

  private OfferFulfillmentEvent createOfferFulfillmentEvent(String rewardSource, boolean isSuccess) {
    OfferFulfillmentEvent offerFulfillmentEvent = new OfferFulfillmentEvent();
    offerFulfillmentEvent.setOfferId("001");
    offerFulfillmentEvent.setCampaignId(111);
    offerFulfillmentEvent.setCampaignName("CampaignName");
    offerFulfillmentEvent.setCustomerNumber("600052");
    offerFulfillmentEvent.setRewardSource(rewardSource);
    offerFulfillmentEvent.setOpcoNumber("813");
    offerFulfillmentEvent.setOfferName("OfferName");
    offerFulfillmentEvent.setRewardValue(500);
    offerFulfillmentEvent.setOfferDescription("OfferDescription");
    offerFulfillmentEvent.setOfferEndDate("2016-08-16");
    offerFulfillmentEvent.setSuccess(isSuccess);
    return offerFulfillmentEvent;
  }

  @Test
  @Tag("accept_dtcPromo")
  void whenDTCAccept_thenSuccess() {
    OfferFulfillmentEvent expectedEvent = createOfferFulfillmentEvent("dtc", true);
    Mockito.when(promoCodesService.createDTCPromoCode(Mockito.any(OfferFulfillmentEvent.class)))
            .thenReturn(mock(PromoCode.class));
    Mockito.when(offerFulfillmentEventInfoService.saveOfferFulfillmentEvents(Mockito.any(OfferFulfillmentEvent.class),
            Mockito.any(PromoCode.class))).thenReturn(mock(
            OfferFulfillmentEventInfo.class));
    this.perksOfferFulfillmentEventConsumer.accept(AvroUtil.getMockConsumerRecord(expectedEvent));
    Mockito.verify(promoCodesService, Mockito.times(1)).createDTCPromoCode(Mockito.any(OfferFulfillmentEvent.class));
  }

  @Test
  @Tag("accept_pfwPromo")
  void whenPFWAccept_thenSuccess() {
    OfferFulfillmentEvent expectedEvent = createOfferFulfillmentEvent("pfw", true);
    this.perksOfferFulfillmentEventConsumer.accept(AvroUtil.getMockConsumerRecord(expectedEvent));
    Mockito.verify(perksFacade, Mockito.times(1)).updateMembership(Mockito.any(OfferFulfillmentEvent.class));
  }

  @Test
  void whenPFWAcceptAndMalformed_thenDoNothing() {
    this.perksOfferFulfillmentEventConsumer.accept(AvroUtil.getMockConsumerRecord(new OfferFulfillmentEvent()));
    Mockito.verify(perksFacade, never()).updateMembership(Mockito.any(OfferFulfillmentEvent.class));
  }

}
