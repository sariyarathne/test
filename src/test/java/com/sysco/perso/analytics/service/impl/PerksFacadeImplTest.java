package com.sysco.perso.analytics.service.impl;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.payload.NewFields;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import com.sysco.perso.analytics.service.CRMService;
import com.sysco.perso.analytics.service.EnrollmentService;
import com.sysco.perso.analytics.service.OfferTrackerService;
import com.sysco.perso.analytics.service.PerksFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@EnableAutoConfiguration
@SpringBootTest
public class PerksFacadeImplTest {

    @Autowired
    PerksFacade perksFacade;

    @MockBean
    EnrollmentService enrollmentService;

    @MockBean
    CRMService crmService;

    @MockBean
    OfferTrackerService offerTrackerService;

    private Enrollment createEnrollment(CustomerInfo customerInfo, OffsetDateTime lastModifiedDate, String rowPayload
            , LocalDate freeTrailEndDate, OffsetDateTime feeWaiverEndDate, Boolean isTrailCompleted) {
        return Enrollment.builder().customerInfo(customerInfo).lastModifiedDate(lastModifiedDate).enrollmentType(
                        EnrollmentType.FREE_TRIAL)
                .rawPayload(rowPayload).freeTrialEndDate(freeTrailEndDate).feeWaiverEndDate(feeWaiverEndDate)
                .isTrialCompleted(isTrailCompleted).isFeeWaive(Boolean.TRUE).build();
    }

    @BeforeEach
    public void beforeEach() {
        CustomerInfo customerInfo = CustomerInfo.builder().customerId("678956").opcoId("000").build();
        Enrollment enrollment = createEnrollment(customerInfo, OffsetDateTime.parse("2021-12-14T00:00:00+00:00"),
                "rowPayload", LocalDate.parse("2016-08-16"),
                OffsetDateTime.parse("2021-12-14T00:00:00+00:00"), false);

        Mockito.doNothing().when(this.crmService).processMembershipFee(ArgumentMatchers.anyList());
        Mockito.when(this.enrollmentService.save(ArgumentMatchers.any(OfferFulfillmentEvent.class))).thenReturn(enrollment);
        Mockito.doNothing().when(this.offerTrackerService).processEnrollmentChanges(ArgumentMatchers.anyList());
    }

    @Test
    @Tag("Enroll")
    void enroll_thenSuccess() throws InterruptedException {
        AccountAttributeChangeEvent event = new AccountAttributeChangeEvent();
        NewFields newFields = new NewFields();
        newFields.setAccountId("100-183956");
        newFields.setStatus("Active");
        newFields.setPerksEnrollmentDate("2022-06-30T00:00:00+00:00");
        event.setNewFields(newFields);
        perksFacade.enroll(event);
        Mockito.verify(crmService, Mockito.times(1)).processMembershipFee(Mockito.anyList());
    }
    @Test
    @Tag("ProcessBatch")
    void processBatch_thenSuccess() throws InterruptedException {
        CustomerInfo customerInfo = CustomerInfo.builder().customerId("678956").opcoId("000").build();
        Enrollment enrollment = createEnrollment(customerInfo, OffsetDateTime.parse("2021-12-14T00:00:00+00:00"),
                "rowPayload", LocalDate.parse("2016-08-16"),
                OffsetDateTime.parse("2021-12-14T00:00:00+00:00"), false);

        perksFacade.processBatch(Collections.singletonList(enrollment));
        Mockito.verify(crmService, Mockito.times(1)).processMembershipFee(Mockito.anyList());
    }

    @Test
    @Tag("updateMembership")
    void updateMembership_thenSuccess() throws InterruptedException {
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

        perksFacade.updateMembership(offerFulfillmentEvent);
        Mockito.verify(crmService, Mockito.times(1)).processMembershipFee(Mockito.anyList());
    }
}
