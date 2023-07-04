package com.sysco.perso.analytics.service;

import com.sysco.perso.analytics.client.statemachine.OfferTrackerClient;
import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.repository.CustomerInfoRepository;
import com.sysco.perso.analytics.repository.EnrollmentRepository;
import com.sysco.perso.analytics.repository.PerksTrackingDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.verify;

@EnableAutoConfiguration
@SpringBootTest
public class OfferTrackerServiceTestIT {
  @Autowired
  EnrollmentRepository enrollmentRepository;
  @Autowired
  CustomerInfoRepository customerInfoRepository;
  @Autowired
  OfferTrackerService offerTrackerService;
  @MockBean
  private OfferTrackerClient offerTrackerClient;

  @SpyBean
  private PerksTrackingDataRepository perksTrackingDataRepository;

  private Enrollment createEnrollmentObject(CustomerInfo customerInfo) {

    Enrollment enrollment = new Enrollment();
    enrollment.setCustomerInfo(customerInfo);
    enrollment.setFreeTrialEndDate(LocalDate.parse("2016-08-16"));
    enrollment.setLastModifiedDate(OffsetDateTime.parse("2021-12-14T00:00:00+00:00"));
    enrollment.setEnrollmentType(EnrollmentType.ENROLLED);
    enrollment.setRawPayload("rawPayload");
    enrollment.setFeeWaiverEndDate(OffsetDateTime.parse("2021-12-14T00:00:00+00:00"));
    enrollment.setFeeWaiverEndDate(OffsetDateTime.parse("2021-12-14T00:00:00+00:00"));
    enrollment.setIsTrialCompleted(true);

    return enrollment;
  }

//  @Test
//  @Tag("SaveTrackerData")
//  @WithMockUser(username = "admin", roles = {"ADMIN"})
//  void whenProcessEnrollmentChanges_thenSuccess() {
//    CustomerInfo customerInfo1 = CustomerInfo.builder().customerId("333823").opcoId("097").build();
//    customerInfoRepository.save(customerInfo1);
//
//    CustomerInfo customerInfo2 = CustomerInfo.builder().customerId("325632").opcoId("035").build();
//    customerInfoRepository.save(customerInfo2);
//
//    Enrollment enrollment1 = createEnrollmentObject(customerInfo1);
//    enrollmentRepository.save(enrollment1);
//
//    Enrollment enrollment2 = createEnrollmentObject(customerInfo2);
//    enrollmentRepository.save(enrollment2);
//
//    List<Enrollment> enrollmentsList = new ArrayList<>();
//    enrollmentsList.add(enrollment1);
//    enrollmentsList.add(enrollment2);
//
//    when(offerTrackerClient.startTracker(anyList())).thenReturn(true);
//    offerTrackerService.processEnrollmentChanges(enrollmentsList);
//
//    verify(perksTrackingDataRepository, times(1)).saveAll(anyList());
//
//  }

}
