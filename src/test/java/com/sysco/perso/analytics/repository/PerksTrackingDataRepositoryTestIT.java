package com.sysco.perso.analytics.repository;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.PerksTrackingData;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.entity.enums.TrackingState;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAutoConfiguration
@SpringBootTest
public class PerksTrackingDataRepositoryTestIT {
  @Autowired
  PerksTrackingDataRepository perksTrackingDataRepository;
  @Autowired
  EnrollmentRepository enrollmentRepository;
  @Autowired
  CustomerInfoRepository customerInfoRepository;

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
    enrollment.setIsFeeWaive(true);

    return enrollment;
  }

  private PerksTrackingData createPerksTrackingData(Enrollment enrollment, LocalDate startDate,
                                                    LocalDate endDate, OffsetDateTime createdDate) {
    return PerksTrackingData.builder().enrollment(enrollment).startDate(startDate).endDate(endDate).trackingState(
            TrackingState.UN_COMPLETED).createdDate(createdDate).build();
  }

  @Test
  @Tag("createPerksTrackingData")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreatePerksTrackingData_thenSuccess() {
    CustomerInfo customerInfo1 = CustomerInfo.builder().customerId("450723").opcoId("007").build();
    customerInfoRepository.save(customerInfo1);

    Enrollment enrollment = createEnrollmentObject(customerInfo1);
    enrollmentRepository.save(enrollment);

    PerksTrackingData expectedPerksTrackingData =
            createPerksTrackingData(enrollment, LocalDate.parse("2019-03-29"), LocalDate.parse("2019-03-29"),
                    OffsetDateTime.parse("2021-12-14T00:00:00+00:00"));
    PerksTrackingData actualPerksTrackingData = perksTrackingDataRepository.save(expectedPerksTrackingData);

    assertEquals(expectedPerksTrackingData.getEnrollment().getCustomerInfo().getCustomerId(),
            actualPerksTrackingData.getEnrollment().getCustomerInfo().getCustomerId());
    assertEquals(expectedPerksTrackingData.getEnrollment().getCustomerInfo().getOpcoId(),
            actualPerksTrackingData.getEnrollment().getCustomerInfo().getOpcoId());
  }

}
