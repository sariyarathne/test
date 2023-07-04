package com.sysco.perso.analytics.service;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.PerksTrackingData;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.entity.enums.TrackingState;
import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.payload.NewFields;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import com.sysco.perso.analytics.repository.CustomerInfoRepository;
import com.sysco.perso.analytics.repository.EnrollmentRepository;
import com.sysco.perso.analytics.repository.PerksTrackingDataRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@EnableAutoConfiguration
@SpringBootTest
public class EnrollmentServiceTestIT {
  @Autowired
  EnrollmentService enrollmentService;

  @Autowired
  CustomerInfoRepository customerInfoRepository;

  @Autowired
  EnrollmentRepository enrollmentRepository;

  @Autowired
  PerksTrackingDataRepository perksTrackingDataRepository;

  private Enrollment createEnrollment(CustomerInfo customerInfo, OffsetDateTime lastModifiedDate, String rowPayload
          , LocalDate freeTrailEndDate, OffsetDateTime feeWaiverEndDate, Boolean isTrailCompleted) {
    return Enrollment.builder().customerInfo(customerInfo).lastModifiedDate(lastModifiedDate).enrollmentType(
                    EnrollmentType.ENROLLED)
            .rawPayload(rowPayload).freeTrialEndDate(freeTrailEndDate).feeWaiverEndDate(feeWaiverEndDate)
            .isTrialCompleted(isTrailCompleted).isFeeWaive(true).build();
  }


  @Test
  @Tag("createEnrollment")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenSaveEnrollment_thenSuccess() {
    CustomerInfo customerInfo1 = CustomerInfo.builder().customerId("183956").opcoId("100").build();

    Enrollment expectedEnrollment =
            createEnrollment(customerInfo1, OffsetDateTime.parse("2021-12-14T00:00:00+00:00"),
                    "rowPayload", LocalDate.parse("2016-08-16"),
                    OffsetDateTime.parse("2021-12-14T00:00:00+00:00"), false);

    AccountAttributeChangeEvent event = new AccountAttributeChangeEvent();
    NewFields newFields = new NewFields();
    newFields.setAccountId("100-183956");
    newFields.setStatus("Active");
    newFields.setPerksEnrollmentDate("2022-06-30T00:00:00+00:00");
    event.setNewFields(newFields);

    customerInfoRepository.save(expectedEnrollment.getCustomerInfo());
    Enrollment actualEnrollment = enrollmentService.save(event);

    assertEquals(expectedEnrollment.getCustomerInfo().getCustomerId(),
            actualEnrollment.getCustomerInfo().getCustomerId());
    assertEquals(expectedEnrollment.getCustomerInfo().getOpcoId(), actualEnrollment.getCustomerInfo().getOpcoId());
  }

  @Test
  @Tag("UpdateEnrollment")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenUpdateEnrollmentWithPerks_thenSuccess() {
    CustomerInfo customerInfo2 = CustomerInfo.builder().customerId("600052").opcoId("813").build();

    Enrollment expectedEnrollment =
            createEnrollment(customerInfo2, OffsetDateTime.parse("2021-12-14T00:00:00+00:00"),
                    "rowPayload", LocalDate.parse("2016-08-16"),
                    OffsetDateTime.parse("2021-12-14T00:00:00+00:00"), false);

    PerksTrackingData perksTrackingData = PerksTrackingData.builder().enrollment(expectedEnrollment).trackingState(
                    TrackingState.IN_PROGRESS).createdDate(OffsetDateTime.parse("2021-12-14T00:00:00+00" +
                    ":00"))
            .endDate(LocalDate.parse("2016-11-16")).startDate(LocalDate.parse("2016-09-16")).build();

    customerInfoRepository.save(customerInfo2);
    enrollmentRepository.save(expectedEnrollment);
    perksTrackingDataRepository.save(perksTrackingData);

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

    Enrollment actualEnrollment = enrollmentService.save(offerFulfillmentEvent);
    assertEquals(expectedEnrollment.getCustomerInfo().getCustomerId(),
            actualEnrollment.getCustomerInfo().getCustomerId());
    assertEquals(expectedEnrollment.getCustomerInfo().getOpcoId(), actualEnrollment.getCustomerInfo().getOpcoId());

  }

  @Test
  @Tag("UpdateEnrollment")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenUpdateEnrollmentWithDtc_thenSuccess() {
    CustomerInfo customerInfo1 = CustomerInfo.builder().customerId("607052").opcoId("060").build();
    customerInfoRepository.save(customerInfo1);

    OfferFulfillmentEvent offerFulfillmentEvent = new OfferFulfillmentEvent();
    offerFulfillmentEvent.setOfferId("001");
    offerFulfillmentEvent.setCampaignId(111);
    offerFulfillmentEvent.setCampaignName("CampaignName");
    offerFulfillmentEvent.setCustomerNumber("607052");
    offerFulfillmentEvent.setRewardSource("dtc");
    offerFulfillmentEvent.setOpcoNumber("060");
    offerFulfillmentEvent.setOfferName("Sysco DTC");
    offerFulfillmentEvent.setRewardValue(500);
    offerFulfillmentEvent.setOfferDescription("OfferDescription");
    offerFulfillmentEvent.setOfferEndDate("2021-12-14T00:00:00+00:00");
    offerFulfillmentEvent.setSuccess(true);

    Enrollment actualEnrollment = enrollmentService.save(offerFulfillmentEvent);
    assertNull(actualEnrollment);

  }

}
