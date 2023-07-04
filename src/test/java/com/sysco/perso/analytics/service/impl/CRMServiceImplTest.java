package com.sysco.perso.analytics.service.impl;

import com.sysco.perso.analytics.client.crm.CRMClient;
import com.sysco.perso.analytics.client.crm.schema.CRMGenericResponse;
import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.SupportRequest;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.repository.SupportRequestRepository;
import com.sysco.perso.analytics.service.CRMService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.when;

@EnableAutoConfiguration
@SpringBootTest
public class CRMServiceImplTest {

  @MockBean
  CRMClient crmClient;
  @Autowired
  CRMService crmService;

  @MockBean
  SupportRequestRepository supportRequestRepository;

  @BeforeEach
  public void beforeEach() {
    ArrayList<CRMGenericResponse> crmGenericResponseArr = new ArrayList<>();
    String[] errors = new String[0];
    CRMGenericResponse crmGenericResponse = new CRMGenericResponse();
    crmGenericResponse.setId("a1w53000000CstNAAS");
    crmGenericResponse.setSuccess(true);
    crmGenericResponse.setErrors(errors);
    crmGenericResponseArr.add(crmGenericResponse);

    final SupportRequest supportRequest = new SupportRequest();
    supportRequest.setId(123L);
    supportRequest.setSrReference("123");
    final Enrollment enrollment = mock(Enrollment.class);
    final CustomerInfo customerInfo = mock(CustomerInfo.class);
    when(customerInfo.getId()).thenReturn(123L);
    when(enrollment.getCustomerInfo()).thenReturn(customerInfo);
    supportRequest.setEnrollment(enrollment);

    when(this.crmClient.waiveOffPerksMembershipFee(ArgumentMatchers.anyString()))
            .thenReturn(Mono.just(crmGenericResponseArr));
    when(this.supportRequestRepository.saveAll(ArgumentMatchers.anyList())).thenReturn(
            Collections.singletonList(supportRequest));

  }

  private Enrollment createEnrollment(CustomerInfo customerInfo, OffsetDateTime lastModifiedDate, String rowPayload
          , LocalDate freeTrailEndDate, OffsetDateTime feeWaiverEndDate, Boolean isTrailCompleted) {
    return Enrollment.builder().customerInfo(customerInfo).lastModifiedDate(lastModifiedDate).enrollmentType(
                    EnrollmentType.FREE_TRIAL)
            .rawPayload(rowPayload).freeTrialEndDate(freeTrailEndDate).feeWaiverEndDate(feeWaiverEndDate)
            .isTrialCompleted(isTrailCompleted).isFeeWaive(Boolean.TRUE).build();
  }

  @Test
  @Tag("processMembershipFee")
  void whenProcessMembershipFee_thenSuccess() throws InterruptedException {
    CustomerInfo customerInfo1 = CustomerInfo.builder().customerId("123456").opcoId("000").build();
    CustomerInfo customerInfo2 = CustomerInfo.builder().customerId("678956").opcoId("000").build();
    List<Enrollment> enrollments = new ArrayList<>();

    enrollments.add(
            createEnrollment(customerInfo1, OffsetDateTime.parse("2021-12-14T00:00:00+00:00"),
                    "rowPayload", LocalDate.parse("2016-07-16"),
                    OffsetDateTime.parse("2021-12-14T00:00:00+00:00"), false));
    enrollments.add(
            createEnrollment(customerInfo2, OffsetDateTime.parse("2021-12-14T00:00:00+00:00"),
                    "rowPayload", LocalDate.parse("2016-07-16"),
                    OffsetDateTime.parse("2021-12-14T00:00:00+00:00"), false));

    when(supportRequestRepository.findBySrCreatedDateBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(
            Collections.emptyList());

    crmService.processMembershipFee(enrollments);
    Mockito.verify(supportRequestRepository, timeout(3000).times(2)).saveAll(Mockito.anyList());
  }

}
