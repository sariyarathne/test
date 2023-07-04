package com.sysco.perso.analytics.it;

import com.sysco.perso.analytics.client.crm.CRMClient;
import com.sysco.perso.analytics.client.crm.schema.CRMGenericResponse;
import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.event.impl.PerksEnrollmentEventConsumer;
import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.repository.CustomerInfoRepository;
import com.sysco.perso.analytics.repository.EnrollmentRepository;
import com.sysco.perso.analytics.repository.PromoCodeRepository;
import com.sysco.perso.analytics.repository.RewardSourceInfoRepository;
import com.sysco.perso.analytics.testutils.AvroUtil;
import com.sysco.perso.analytics.testutils.FileUtil;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@EnableAutoConfiguration()
@SpringBootTest()
@DisplayName("Test perks enrollment event received")
@TestInstance(PER_CLASS)
@Sql({"classpath:test_data/reward_source.sql"})
@Disabled
public class PerksEnrollmentEventIT {

  @Autowired
  private RewardSourceInfoRepository rewardSourceInfoRepository;

  @Autowired
  private PromoCodeRepository promoCodeRepository;

  @Autowired
  private CustomerInfoRepository customerInfoRepository;

  @Autowired
  private EnrollmentRepository enrollmentRepository;

  @Autowired
  private PerksEnrollmentEventConsumer perksEnrollmentEventConsumer;

  @MockBean
  private CRMClient mockCrmClient;

  private final FileUtil fileUtil = new FileUtil();

  @BeforeAll
  void beforeAll() {

  }

  @Nested
  @DisplayName("when event is enrolled free trial and no exiting enrollment")
  class EnrolledFreeTrial {

    @Test()
    @DisplayName("then waive off perks membership fee and create perks promo code immediately")
    void test1() throws InterruptedException {
      CRMGenericResponse response1 = new CRMGenericResponse();
      response1.setId("123");
      response1.setSuccess(true);
      CRMGenericResponse response2 = new CRMGenericResponse();
      response1.setId("234");
      response1.setSuccess(true);
      List<CRMGenericResponse> supportRequestList = Lists.list(response1, response2);
      when(mockCrmClient.waiveOffPerksMembershipFee(anyString())).thenReturn(Mono.just(supportRequestList));

      AccountAttributeChangeEvent accountAttributeChangeEvent =
              fileUtil.readJson("/test_data/enrolled_free_trial_event.json", AccountAttributeChangeEvent.class);
      perksEnrollmentEventConsumer.accept(
              AvroUtil.getMockConsumerRecord(accountAttributeChangeEvent));
      Thread.sleep(3000);

      Optional<CustomerInfo> customerInfo = customerInfoRepository.findByCustomerIdAndOpcoId("951290", "022");
      assertTrue(customerInfo.isPresent());

      Optional<Enrollment> enrollment =
              enrollmentRepository.findByCustomerInfo(customerInfo.get()).stream().findFirst();
      assertTrue(enrollment.isPresent());

      assertEquals(EnrollmentType.FREE_TRIAL, enrollment.get().getEnrollmentType());
      verify(mockCrmClient, times(1)).waiveOffPerksMembershipFee(anyString());
    }

  }

  @Nested
  @DisplayName("when event is enrolled")
  class EnrolledInPilot {

  }

  @AfterAll
  @Sql({"classpath:test_data/clear.sql"})
  void afterAll() {

  }

}
