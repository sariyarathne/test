package com.sysco.perso.analytics.service;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.OfferFulfillmentEventInfo;
import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.entity.RewardSourceInfo;
import com.sysco.perso.analytics.entity.enums.ModifiedSource;
import com.sysco.perso.analytics.entity.enums.PromoCodeStatus;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import com.sysco.perso.analytics.repository.CustomerInfoRepository;
import com.sysco.perso.analytics.repository.PromoCodeRepository;
import com.sysco.perso.analytics.repository.RewardSourceInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@EnableAutoConfiguration
@SpringBootTest
public class OfferFulfillmentEventInfoServiceTestIT {
  @Autowired
  OfferFulfillmentEventInfoService offerFulfillmentEventInfoService;
  @Autowired
  private CustomerInfoRepository customerInfoRepository;
  @Autowired
  private PromoCodeRepository promoCodeRepository;
  @Autowired
  private RewardSourceInfoRepository rewardSourceInfoRepository;

  @Test
  @Tag("saveOfferFulfillmentEvents")
  @DisplayName("Verify saveOfferFulfillmentEvents with dtc events")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenSaveOfferFulfillmentEventInfoWithDtcEvent_thenSuccess() {

    final CustomerInfo customerInfo =
            customerInfoRepository.save(CustomerInfo.builder().customerId("6273849").opcoId("253").build());

    final RewardSourceInfo rewardSourceInfo = rewardSourceInfoRepository.findById("1").orElse(null);

    PromoCode savedPromoCode = promoCodeRepository.save(
            PromoCode.builder().rewardSourceInfo(rewardSourceInfo).customerInfo(customerInfo).promoCodeStatus(
                            PromoCodeStatus.ACTIVE).code("code").codeEndDate(OffsetDateTime.now())
                    .codeStartDate(OffsetDateTime.now()).generatedDate(OffsetDateTime.now())
                    .lastModifiedDate(OffsetDateTime.now()).modifiedSource(ModifiedSource.PE_ANALYTICS)
                    .description("dtc").minimumBucketSize(2).reward(100).rewardCurrency("USD").name("dtc")
                    .uuid("uuidOffer").build());


    OfferFulfillmentEvent offerFulfillmentEvent = new OfferFulfillmentEvent();
    offerFulfillmentEvent.setOfferId("001");
    offerFulfillmentEvent.setCampaignId(111);
    offerFulfillmentEvent.setCampaignName("CampaignName");
    offerFulfillmentEvent.setCustomerNumber("600052");
    offerFulfillmentEvent.setRewardSource("dtc");
    offerFulfillmentEvent.setOpcoNumber("813");
    offerFulfillmentEvent.setOfferName("OfferName");
    offerFulfillmentEvent.setRewardValue(500);
    offerFulfillmentEvent.setOfferDescription("OfferDescription");
    offerFulfillmentEvent.setOfferEndDate("2016-08-16");
    offerFulfillmentEvent.setSuccess(true);


    offerFulfillmentEventInfoService.saveOfferFulfillmentEvents(offerFulfillmentEvent, savedPromoCode);
  }

  @Test
  @Tag("saveOfferFulfillmentEvents")
  @DisplayName("Verify saveOfferFulfillmentEvents with perks events")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenSaveOfferFulfillmentEventInfoWithPerksEvent_thenSuccess() {

    OfferFulfillmentEvent offerFulfillmentEvent = new OfferFulfillmentEvent();
    offerFulfillmentEvent.setOfferId("001");
    offerFulfillmentEvent.setCampaignId(111);
    offerFulfillmentEvent.setCampaignName("CampaignName");
    offerFulfillmentEvent.setCustomerNumber("600052");
    offerFulfillmentEvent.setRewardSource("pfw");
    offerFulfillmentEvent.setOpcoNumber("813");
    offerFulfillmentEvent.setOfferName("OfferName");
    offerFulfillmentEvent.setRewardValue(500);
    offerFulfillmentEvent.setOfferDescription("OfferDescription");
    offerFulfillmentEvent.setOfferEndDate("2016-08-16");
    offerFulfillmentEvent.setSuccess(true);

    OfferFulfillmentEventInfo offerFulfillmentEvents =
            offerFulfillmentEventInfoService.saveOfferFulfillmentEvents(offerFulfillmentEvent, null);
    assertNotNull(offerFulfillmentEvents.getId());
  }

}
