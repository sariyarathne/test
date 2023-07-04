package com.sysco.perso.analytics.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.EnrollmentHistory;
import com.sysco.perso.analytics.entity.PerksTrackingData;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.entity.enums.TrackingState;
import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import com.sysco.perso.analytics.event.payload.PerksEnrollType;
import com.sysco.perso.analytics.repository.CustomerInfoRepository;
import com.sysco.perso.analytics.repository.EnrollmentHistoryRepository;
import com.sysco.perso.analytics.repository.EnrollmentRepository;
import com.sysco.perso.analytics.repository.PerksTrackingDataRepository;
import com.sysco.perso.analytics.service.EnrollmentService;
import com.sysco.perso.analytics.service.PromoCodesService;
import com.sysco.perso.analytics.util.CustomerInfoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

  private static final Logger logger = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

  private final PromoCodesService promoCodesService;
  private final EnrollmentRepository enrollmentRepository;

  private final EnrollmentHistoryRepository enrollmentHistoryRepository;
  private final CustomerInfoRepository customerInfoRepository;

  private final PerksTrackingDataRepository perksTrackingDataRepository;


  @Autowired
  public EnrollmentServiceImpl(PromoCodesService promoCodesService, final EnrollmentRepository enrollmentRepository,
                               final EnrollmentHistoryRepository enrollmentHistoryRepository,
                               final CustomerInfoRepository customerInfoRepository,
                               PerksTrackingDataRepository perksTrackingDataRepository) {
    this.promoCodesService = promoCodesService;
    this.enrollmentRepository = enrollmentRepository;
    this.enrollmentHistoryRepository = enrollmentHistoryRepository;
    this.customerInfoRepository = customerInfoRepository;
    this.perksTrackingDataRepository = perksTrackingDataRepository;
  }

  @Override
  public Enrollment save(final AccountAttributeChangeEvent event) {

    Enrollment enrollment;
    CustomerInfo existingCustomer;
    final String eventPayload = getJsonString(event);
    final EnrollmentType enrollmentType = getEnrollmentType(event.getNewFields().getSyscoPerks());

    CustomerInfo customerInfo =
            CustomerInfoUtil.splitCustomerDetailsToCustomerIdAndOpcoId(event.getNewFields().getAccountId());
    Optional<CustomerInfo> customerInfoResult =
            customerInfoRepository.findByCustomerIdAndOpcoId(customerInfo.getCustomerId(), customerInfo.getOpcoId());

    if (customerInfoResult.isEmpty()) {
      existingCustomer = customerInfoRepository.save(customerInfo);
    } else {
      existingCustomer = customerInfoResult.get();
    }

    Optional<Enrollment> optionalEnrollment =
            enrollmentRepository.findByCustomerInfo(existingCustomer).stream().findFirst();

    if (optionalEnrollment.isPresent()) {
      enrollment = saveExistingEnrollment(eventPayload, enrollmentType, optionalEnrollment.get());
    } else {
      enrollment = saveNewEnrollment(existingCustomer, eventPayload, enrollmentType);
      promoCodesService.createInitialPerksPromoCode(existingCustomer);
    }

    EnrollmentHistory enrollmentHistory =
            EnrollmentHistory.builder().enrollment(enrollment).enrollmentType(enrollmentType)
                    .isFeeWaive(enrollment.getIsFeeWaive()).rawPayload(eventPayload)
                    .createdDate(OffsetDateTime.now()).build();
    enrollmentHistoryRepository.save(enrollmentHistory);

    return enrollment;
  }

  private Enrollment saveExistingEnrollment(String eventPayload, EnrollmentType enrollmentType,
                                            Enrollment alreadyExistingEnrollment) {
    if (!alreadyExistingEnrollment.getIsTrialCompleted() &&
            EnrollmentType.ENROLLED == alreadyExistingEnrollment.getEnrollmentType() &&
            EnrollmentType.FREE_TRIAL == enrollmentType) {
      alreadyExistingEnrollment.setEnrollmentType(enrollmentType);
      alreadyExistingEnrollment.setFreeTrialEndDate(getFreeTrailEndDate());
    }

    alreadyExistingEnrollment.setLastModifiedDate(OffsetDateTime.now());
    alreadyExistingEnrollment.setRawPayload(eventPayload);
    final Enrollment enrollment = enrollmentRepository.save(alreadyExistingEnrollment);
    logger.info("Enrollment with ID {} is updated", enrollment.getId());
    return enrollment;
  }

  private Enrollment saveNewEnrollment(CustomerInfo existingCustomer, String eventPayload,
                                       EnrollmentType enrollmentType) {
    Enrollment newEnrollment = new Enrollment();
    newEnrollment.setCustomerInfo(existingCustomer);
    newEnrollment.setLastModifiedDate(OffsetDateTime.now());
    newEnrollment.setEnrollmentType(enrollmentType);

    newEnrollment.setRawPayload(eventPayload);

    if (enrollmentType == EnrollmentType.FREE_TRIAL) {
      newEnrollment.setFreeTrialEndDate(getFreeTrailEndDate());
    } else {
      newEnrollment.setFreeTrialEndDate(OffsetDateTime.now().plusYears(1).toLocalDate());
    }
    newEnrollment.setFeeWaiverEndDate(OffsetDateTime.now());
    newEnrollment.setIsTrialCompleted(false);
    newEnrollment.setIsFeeWaive(true);

    final Enrollment enrollment = enrollmentRepository.save(newEnrollment);
    logger.info("Enrollment created with ID {}", enrollment.getId());
    return enrollment;
  }

  private EnrollmentType getEnrollmentType(final String enrollType) {
    if (Objects.equals(enrollType, PerksEnrollType.ENROLLED.getValue())) {
      return EnrollmentType.ENROLLED;
    }
    return EnrollmentType.FREE_TRIAL;
  }

  private String getJsonString(AccountAttributeChangeEvent event) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    String result = "";
    try {
      result = objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException e) {
      logger.error("Can not convert to AccountAttributeChangeEvent to json string {}", e.getMessage());
    }
    return result;
  }

  @Override
  public Enrollment save(final OfferFulfillmentEvent event) {

    final String customerNumber = event.getCustomerNumber();
    final String OpCoNumber = event.getOpcoNumber();
    Enrollment enrollment = null;

    Optional<CustomerInfo> optionalCustomerInfoResult =
            customerInfoRepository.findByCustomerIdAndOpcoId(customerNumber, OpCoNumber);

    if (optionalCustomerInfoResult.isPresent()) {
      Optional<Enrollment> optionalEnrollment =
              enrollmentRepository.findByCustomerInfo(optionalCustomerInfoResult.get()).stream().findFirst();
      if (optionalEnrollment.isPresent()) {
        Enrollment alreadyExistingEnrollment = optionalEnrollment.get();
        alreadyExistingEnrollment.setLastModifiedDate(OffsetDateTime.now());
        alreadyExistingEnrollment.setEnrollmentType(EnrollmentType.ENROLLED);
        alreadyExistingEnrollment.setIsTrialCompleted(true);
        enrollment = enrollmentRepository.save(alreadyExistingEnrollment);
      } else {
        logger.error("Enrollment not found with CustomerNumber {} and OpCoNumber {}", customerNumber, OpCoNumber);
        return null;
      }

      Optional<PerksTrackingData> optionalPerksTrackingData =
              perksTrackingDataRepository.findByEnrollmentOrderByCreatedDate(enrollment).stream().findFirst();
      if (optionalPerksTrackingData.isPresent()) {
        PerksTrackingData existingPerksTrackingData = optionalPerksTrackingData.get();
        existingPerksTrackingData.setTrackingState(TrackingState.COMPLETED);
        perksTrackingDataRepository.save(existingPerksTrackingData);
      } else {
        logger.error("Tracking Data not found with Trial CustomerNumber {} and OpCoNumber {}", customerNumber,
                OpCoNumber);
      }
    }

    return enrollment;
  }

  private LocalDate getFreeTrailEndDate() {
    final LocalDate offerStartDate = OffsetDateTime.now().toLocalDate().with(TemporalAdjusters.firstDayOfNextMonth());
    return offerStartDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
  }
}
