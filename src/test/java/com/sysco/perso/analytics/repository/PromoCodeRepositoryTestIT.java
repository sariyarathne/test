package com.sysco.perso.analytics.repository;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.entity.PromoCodeHistory;
import com.sysco.perso.analytics.entity.RewardSourceInfo;
import com.sysco.perso.analytics.entity.enums.PromoCodeStatus;
import com.sysco.perso.analytics.entity.enums.ModifiedSource;
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
public class PromoCodeRepositoryTestIT {
  @Autowired
  private PromoCodeRepository promoCodeRepository;

  @Autowired
  private RewardSourceInfoRepository rewardSourceInfoRepository;

  @Autowired
  private CustomerInfoRepository customerInfoRepository;

  @Autowired
  private PromoCodeHistoryRepository promoCodeHistoryRepository;


  @Test
  @Tag("createPromoCode")
  @DisplayName("Verify save promo code")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreatePromoCode_thenSuccess() {
    final CustomerInfo customerInfo =
            customerInfoRepository.save(CustomerInfo.builder().customerId("645647").opcoId("123").build());

    final RewardSourceInfo rewardSourceInfo =
            rewardSourceInfoRepository.save(
                    RewardSourceInfo.builder().rewardSourceId("1").rewardSource("dtc").build());

    final PromoCode promoCode = promoCodeRepository.save(
            PromoCode.builder().rewardSourceInfo(rewardSourceInfo).customerInfo(customerInfo).promoCodeStatus(
                            PromoCodeStatus.ACTIVE).code("PERKS2022ABC").codeEndDate(OffsetDateTime.now())
                    .codeStartDate(OffsetDateTime.now()).generatedDate(OffsetDateTime.now())
                    .lastModifiedDate(OffsetDateTime.now()).modifiedSource(ModifiedSource.PE_ANALYTICS)
                    .description("perks").minimumBucketSize(2).reward(100).rewardCurrency("USD").name("Perks")
                    .uuid("123-234-345-456").build());
    assertNotNull(promoCode.getUuid());
  }

  @Test
  @Tag("createPromoCodeHistory")
  @DisplayName("Verify save promo code history")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreatePromoCodeHistory_thenSuccess() {
    final CustomerInfo customerInfo =
            customerInfoRepository.save(CustomerInfo.builder().customerId("645646").opcoId("123").build());

    final RewardSourceInfo rewardSourceInfo =
            rewardSourceInfoRepository.save(
                    RewardSourceInfo.builder().rewardSourceId("2").rewardSource("perks").build());

    final PromoCode promoCode = promoCodeRepository.save(
            PromoCode.builder().rewardSourceInfo(rewardSourceInfo).customerInfo(customerInfo).promoCodeStatus(
                            PromoCodeStatus.ACTIVE).code("PERKS2022ABC").codeEndDate(OffsetDateTime.now())
                    .codeStartDate(OffsetDateTime.now()).generatedDate(OffsetDateTime.now())
                    .lastModifiedDate(OffsetDateTime.now()).modifiedSource(ModifiedSource.PE_ANALYTICS)
                    .description("perks").minimumBucketSize(2).reward(100).rewardCurrency("USD").name("Perks")
                    .uuid("123-234-345-234").build());

    final PromoCodeHistory promoCodeHistory = promoCodeHistoryRepository.save(
            PromoCodeHistory.builder().promoCode(promoCode).promoCodeStatus(PromoCodeStatus.ACTIVE)
                    .modifiedSource(ModifiedSource.PE_ANALYTICS)
                    .date(OffsetDateTime.now()).build());

    assertNotNull(promoCodeHistory.getId());
  }

}
