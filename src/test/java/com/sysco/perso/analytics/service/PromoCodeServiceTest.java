package com.sysco.perso.analytics.service;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.entity.PromoCodeHistory;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.entity.enums.PromoCodeStatus;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import com.sysco.perso.analytics.repository.CustomerInfoRepository;
import com.sysco.perso.analytics.repository.PromoCodeHistoryRepository;
import com.sysco.perso.analytics.repository.PromoCodeRepository;
import com.sysco.perso.analytics.repository.RewardSourceInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.sysco.perso.analytics.util.PromoCodeUtil.getCurrentPerksPromoCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EnableAutoConfiguration
@SpringBootTest
public class PromoCodeServiceTest {

  @MockBean
  private PromoCodeRepository mockPromoCodeRepository;

  @MockBean
  private PromoCodeHistoryRepository mockPromoCodeHistoryRepository;

  @MockBean
  private CustomerInfoRepository mockCustomerInfoRepository;

  @Autowired
  PromoCodesService promoCodesService;

  @Mock
  List<PromoCode> mockPromoCodes = new ArrayList<>();

  @Test
  @Tag("createInitialPerksPromoCode")
  @DisplayName("Verify createInitialPerksPromoCode for given CustomerInfo")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreateInitialPerksPromoCode_thenSuccess() {

    CustomerInfo customerInfo = mock(CustomerInfo.class);
    PromoCode savedPromoCode = mock(PromoCode.class);
    PromoCodeHistory savedPromoCodeHistory = mock(PromoCodeHistory.class);

    when(mockPromoCodeRepository.save(Mockito.any(PromoCode.class))).thenReturn(savedPromoCode);
    when(mockPromoCodeHistoryRepository.save(Mockito.any(PromoCodeHistory.class))).thenReturn(savedPromoCodeHistory);
    promoCodesService.createInitialPerksPromoCode(customerInfo);

    verify(mockPromoCodeRepository, times(1)).save(any(PromoCode.class));
    verify(mockPromoCodeHistoryRepository, times(1)).save(any(PromoCodeHistory.class));

  }

  @Test
  @Tag("createDTCPromoCode")
  @DisplayName("Verify CreateDTCPromoCode when CustomerInfoResult isEmpty for given OfferFulfillmentEvent")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreateDTCPromoCodeWithCustomerInfoResultIsEmpty_thenSuccess() {

    CustomerInfo mockCustomerInfo = mock(CustomerInfo.class);
    PromoCode savedDTCPromoCode = mock(PromoCode.class);
    PromoCodeHistory savedPromoCodeHistory = mock(PromoCodeHistory.class);

    OfferFulfillmentEvent event = new OfferFulfillmentEvent();
    event.setOfferId("OfferId");
    event.setCampaignId(100);
    event.setCustomerNumber("671284");
    event.setOpcoNumber("912");
    event.setRewardSource("RewardSource");
    event.setOfferName("OfferName");
    event.setRewardValue(500);
    event.setOfferDescription("OfferDescription");
    event.setSuccess(true);
    event.setOfferEndDate("2021-12-14T00:00:00+00:00");

    when(mockCustomerInfoRepository.findByCustomerIdAndOpcoId(anyString(), anyString())).thenReturn(Optional.empty());
    when(mockCustomerInfoRepository.save(Mockito.any(CustomerInfo.class))).thenReturn(mockCustomerInfo);
    when(mockPromoCodeRepository.save(Mockito.any(PromoCode.class))).thenReturn(savedDTCPromoCode);
    when(mockPromoCodeHistoryRepository.save(Mockito.any(PromoCodeHistory.class))).thenReturn(savedPromoCodeHistory);
    promoCodesService.createDTCPromoCode(event);

    verify(mockPromoCodeRepository, times(1)).save(any(PromoCode.class));
    verify(mockPromoCodeHistoryRepository, times(1)).save(any(PromoCodeHistory.class));

  }

  @Test
  @Tag("createDTCPromoCode")
  @DisplayName("Verify CreateDTCPromoCode when CustomerInfoResult is not Empty for given OfferFulfillmentEvent")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreateDTCPromoCodeWithCustomerInfoResultIsNotEmpty_thenSuccess() {

    CustomerInfo mockCustomerInfo = mock(CustomerInfo.class);
    PromoCode savedDTCPromoCode = mock(PromoCode.class);
    PromoCodeHistory savedPromoCodeHistory = mock(PromoCodeHistory.class);

    OfferFulfillmentEvent event = new OfferFulfillmentEvent();
    event.setOfferId("OfferId");
    event.setCampaignId(100);
    event.setCustomerNumber("671284");
    event.setOpcoNumber("912");
    event.setRewardSource("RewardSource");
    event.setOfferName("OfferName");
    event.setRewardValue(500);
    event.setOfferDescription("OfferDescription");
    event.setSuccess(true);
    event.setOfferEndDate("2021-12-14T00:00:00+00:00");

    when(mockCustomerInfoRepository.findByCustomerIdAndOpcoId(anyString(), anyString())).thenReturn(
            Optional.ofNullable(mockCustomerInfo));
    when(mockCustomerInfoRepository.save(Mockito.any(CustomerInfo.class))).thenReturn(mockCustomerInfo);
    when(mockPromoCodeRepository.save(Mockito.any(PromoCode.class))).thenReturn(savedDTCPromoCode);
    when(mockPromoCodeHistoryRepository.save(Mockito.any(PromoCodeHistory.class))).thenReturn(savedPromoCodeHistory);
    promoCodesService.createDTCPromoCode(event);

    verify(mockPromoCodeRepository, times(1)).save(any(PromoCode.class));
    verify(mockPromoCodeHistoryRepository, times(1)).save(any(PromoCodeHistory.class));

  }


  @Test
  @Tag("createBatchPerksPromoCodes")
  @DisplayName("Verify CreateBatchPerksPromoCodes for given List of Enrollments")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreateBatchPerksPromoCodes_thenSuccess() {

    CustomerInfo customerInfo1 = CustomerInfo.builder().customerId("183956").opcoId("100").build();
    PromoCode savedDTCPromoCode = mock(PromoCode.class);
    PromoCodeHistory savedPromoCodeHistory = mock(PromoCodeHistory.class);

    Enrollment enrollment =
            Enrollment.builder().customerInfo(customerInfo1)
                    .lastModifiedDate(OffsetDateTime.parse("2021-12-14T00:00:00+00:00"))
                    .enrollmentType(EnrollmentType.FREE_TRIAL).rawPayload("rawPayload")
                    .freeTrialEndDate(LocalDate.parse("2021-12-14"))
                    .feeWaiverEndDate(OffsetDateTime.parse("2021-12-14T00:00:00+00:00")).isTrialCompleted(false)
                    .isFeeWaive(false)
                    .build();

    List<Enrollment> enrollments = new ArrayList<>();
    enrollments.add(enrollment);

    List<PromoCode> savedPromoCodes = new ArrayList<>();
    savedPromoCodes.add(savedDTCPromoCode);

    List<PromoCodeHistory> savedPromoCodesHistory = new ArrayList<>();
    savedPromoCodesHistory.add(savedPromoCodeHistory);

    when(mockPromoCodeRepository.saveAll(anyList())).thenReturn(savedPromoCodes);
    when(mockPromoCodeHistoryRepository.saveAll(anyList())).thenReturn(savedPromoCodesHistory);
    promoCodesService.createBatchPerksPromoCodes(enrollments);

    verify(mockPromoCodeRepository, times(1)).saveAll(anyList());
    verify(mockPromoCodeHistoryRepository, times(1)).saveAll(anyList());

  }
  @Test
  @Tag("createBatchPerksPromoCodes")
  @DisplayName("Verify CreateBatchPerksPromoCodes for given List of Enrollments when promoCode already exists")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void WhenPerksPromoCodesExit_thenNotCreateDuplicateCodeSuccess() {

    CustomerInfo customerInfo1 = CustomerInfo.builder().customerId("1830562").opcoId("105").build();
    PromoCode existPerksPromoCode = PromoCode.builder().customerInfo(customerInfo1).code(getCurrentPerksPromoCode()).build();

    Enrollment enrollment =
            Enrollment.builder().customerInfo(customerInfo1)
                    .lastModifiedDate(OffsetDateTime.parse("2021-12-14T00:00:00+00:00"))
                    .enrollmentType(EnrollmentType.FREE_TRIAL).rawPayload("rawPayload")
                    .freeTrialEndDate(LocalDate.parse("2021-12-14"))
                    .feeWaiverEndDate(OffsetDateTime.parse("2021-12-14T00:00:00+00:00")).isTrialCompleted(false)
                    .isFeeWaive(false)
                    .build();

    List<Enrollment> enrollments = new ArrayList<>();
    enrollments.add(enrollment);
    List<PromoCode> savedPromoCodes = new ArrayList<>();
    savedPromoCodes.add(existPerksPromoCode);

    when(mockPromoCodeRepository.findByCode(anyString())).thenReturn(savedPromoCodes);
    when(mock(HashSet.class).contains(anyString())).thenReturn(true);
    List<PromoCode> resultPromoCodes = promoCodesService.createBatchPerksPromoCodes(enrollments);

    assert(resultPromoCodes.isEmpty());

  }

  @Test
  @Tag("processPromoCodesChanges")
  @DisplayName("Verify processPromoCodesChanges for given List of PromoCodes with INIT status")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenProcessPromoCodesChangesWithStartDateAndInit_thenSuccess() {

    PromoCode promoCodeWithInitStatus = mock(PromoCode.class);
    PromoCodeHistory savedPromoCodeHistory = mock(PromoCodeHistory.class);

    List<PromoCode> promoCodesList = new ArrayList<>();
    promoCodesList.add(promoCodeWithInitStatus);

    List<PromoCodeHistory> promoCodesHistoryList = new ArrayList<>();
    promoCodesHistoryList.add(savedPromoCodeHistory);

    when(promoCodeWithInitStatus.getPromoCodeStatus()).thenReturn(PromoCodeStatus.INIT);
    when(promoCodeWithInitStatus.getCodeStartDate()).thenReturn(OffsetDateTime.now().minusDays(1));
    when(mockPromoCodeRepository.saveAll(anyList())).thenReturn(promoCodesList);
    when(mockPromoCodeHistoryRepository.saveAll(anyList())).thenReturn(promoCodesHistoryList);
    promoCodesService.processPromoCodesChanges(promoCodesList);

    verify(promoCodeWithInitStatus, times(1)).setPromoCodeStatus(PromoCodeStatus.ACTIVE);

  }

  @Test
  @Tag("processPromoCodesChanges")
  @DisplayName("Verify processPromoCodesChanges for given List of PromoCodes with ACTIVE status")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenProcessPromoCodesChangesWithEndDateAndNotInit_thenSuccess() {

    PromoCode promoCodeWithNotInitStatus = mock(PromoCode.class);
    PromoCodeHistory savedPromoCodeHistory = mock(PromoCodeHistory.class);

    List<PromoCode> promoCodesList = new ArrayList<>();
    promoCodesList.add(promoCodeWithNotInitStatus);

    List<PromoCodeHistory> promoCodesHistoryList = new ArrayList<>();
    promoCodesHistoryList.add(savedPromoCodeHistory);

    when(promoCodeWithNotInitStatus.getPromoCodeStatus()).thenReturn(PromoCodeStatus.ACTIVE);
    when(promoCodeWithNotInitStatus.getCodeEndDate()).thenReturn(OffsetDateTime.now());
    when(mockPromoCodeRepository.saveAll(anyList())).thenReturn(promoCodesList);
    when(mockPromoCodeHistoryRepository.saveAll(anyList())).thenReturn(promoCodesHistoryList);
    promoCodesService.processPromoCodesChanges(promoCodesList);

    verify(promoCodeWithNotInitStatus, times(1)).setPromoCodeStatus(PromoCodeStatus.EXPIRED);

  }

}
