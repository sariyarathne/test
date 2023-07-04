package com.sysco.perso.analytics.service.impl;

import com.sysco.perso.analytics.client.statemachine.OfferTrackerClient;
import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.repository.PerksTrackingDataRepository;
import com.sysco.perso.analytics.service.OfferTrackerService;
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
import java.util.ArrayList;
import java.util.List;

@EnableAutoConfiguration
@SpringBootTest
public class OfferTrackerServiceImplTest {

  @Autowired
  OfferTrackerService offerTrackerService;

  @MockBean
  OfferTrackerClient offerTrackerClient;

  @MockBean
  PerksTrackingDataRepository perksTrackingDataRepository;


  private Enrollment createEnrollment(CustomerInfo customerInfo, OffsetDateTime lastModifiedDate, String rowPayload
          , LocalDate freeTrailEndDate, OffsetDateTime feeWaiverEndDate, Boolean isTrailCompleted) {
    return Enrollment.builder().customerInfo(customerInfo).lastModifiedDate(lastModifiedDate).enrollmentType(
                    EnrollmentType.FREE_TRIAL)
            .rawPayload(rowPayload).freeTrialEndDate(freeTrailEndDate).feeWaiverEndDate(feeWaiverEndDate)
            .isTrialCompleted(isTrailCompleted).isFeeWaive(Boolean.TRUE).build();
  }

  @BeforeEach
  public void beforeEach() {
    Mockito.when(this.offerTrackerClient.startTracker(ArgumentMatchers.any())).thenReturn(Boolean.TRUE);
    Mockito.when(this.perksTrackingDataRepository.saveAll(ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
  }

  @Test
  @Tag("processEnrollmentChanges")
  void whenProcessEnrollmentChanges_thenSuccess() {
    CustomerInfo customerInfo1 = CustomerInfo.builder().customerId("123456").opcoId("000").build();
    CustomerInfo customerInfo2 = CustomerInfo.builder().customerId("678956").opcoId("000").build();
    List<Enrollment> enrollments = new ArrayList<>();

    enrollments.add(
            createEnrollment(customerInfo1, OffsetDateTime.parse("2021-12-14T00:00:00+00:00"),
                    "rowPayload", LocalDate.parse("2016-08-16"),
                    OffsetDateTime.parse("2021-12-14T00:00:00+00:00"), false));
    enrollments.add(
            createEnrollment(customerInfo2, OffsetDateTime.parse("2021-12-14T00:00:00+00:00"),
                    "rowPayload", LocalDate.parse("2016-08-16"),
                    OffsetDateTime.parse("2021-12-14T00:00:00+00:00"), false));


    offerTrackerService.processEnrollmentChanges(enrollments);
    Mockito.verify(perksTrackingDataRepository, Mockito.times(1)).saveAll(Mockito.anyList());

  }
}
