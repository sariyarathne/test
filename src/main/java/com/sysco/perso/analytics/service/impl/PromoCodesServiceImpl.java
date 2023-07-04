package com.sysco.perso.analytics.service.impl;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.entity.PromoCodeHistory;
import com.sysco.perso.analytics.entity.RewardSourceInfo;
import com.sysco.perso.analytics.entity.enums.ModifiedSource;
import com.sysco.perso.analytics.entity.enums.PromoCodeStatus;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import com.sysco.perso.analytics.repository.CustomerInfoRepository;
import com.sysco.perso.analytics.repository.PromoCodeHistoryRepository;
import com.sysco.perso.analytics.repository.PromoCodeRepository;
import com.sysco.perso.analytics.repository.RewardSourceInfoRepository;
import com.sysco.perso.analytics.service.PromoCodesService;
import com.sysco.perso.analytics.util.PromoCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sysco.perso.analytics.util.PromoCodeUtil.getCurrentPerksPromoCode;

@Service
@Transactional
public class PromoCodesServiceImpl implements PromoCodesService {

  private static final Logger logger = LoggerFactory.getLogger(PromoCodesServiceImpl.class);
  private final PromoCodeRepository promoCodeRepository;
  private final PromoCodeHistoryRepository promoCodeHistoryRepository;
  private final RewardSourceInfoRepository rewardSourceInfoRepository;

  private final CustomerInfoRepository customerInfoRepository;


  @Autowired
  public PromoCodesServiceImpl(final PromoCodeRepository promoCodeRepository,
                               final PromoCodeHistoryRepository promoCodeHistoryRepository,
                               final RewardSourceInfoRepository rewardSourceInfoRepository,
                               final CustomerInfoRepository customerInfoRepository) {
    this.promoCodeRepository = promoCodeRepository;
    this.promoCodeHistoryRepository = promoCodeHistoryRepository;
    this.rewardSourceInfoRepository = rewardSourceInfoRepository;
    this.customerInfoRepository = customerInfoRepository;

  }


  @Override
  public PromoCode createInitialPerksPromoCode(final CustomerInfo customerInfo) {
    PromoCode perksPromoCode = getPerksPromoCode(customerInfo);
    return savePromoCodesAndHistory(perksPromoCode);
  }

  private PromoCode getPerksPromoCode(final CustomerInfo customerInfo) {
    RewardSourceInfo perksRewardSource = rewardSourceInfoRepository.getById("2");

    return PromoCode.builder().uuid(PromoCodeUtil.getPromoCodeUUID()).code(getCurrentPerksPromoCode())
            .name("Sysco Perks")
            .description("Save $100 on orders over $1000! Use code:").reward(100).rewardCurrency("USD")
            .minimumBucketSize(1000)
            .promoCodeStatus(PromoCodeStatus.ACTIVE).codeStartDate(
                    OffsetDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0)
                            .withSecond(1))
            .codeEndDate(OffsetDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59)
                    .withSecond(59))
            .generatedDate(OffsetDateTime.now())
            .modifiedSource(ModifiedSource.PE_ANALYTICS)
            .lastModifiedDate(OffsetDateTime.now())
            .customerInfo(customerInfo).rewardSourceInfo(perksRewardSource).build();
  }

  private PromoCode savePromoCodesAndHistory(final PromoCode perksPromoCode) {
    final PromoCode savedPerksPromoCode = promoCodeRepository.save(perksPromoCode);

    PromoCodeHistory promoCodesHistory =
            PromoCodeHistory.builder().promoCode(savedPerksPromoCode)
                    .promoCodeStatus(perksPromoCode.getPromoCodeStatus())
                    .modifiedSource(ModifiedSource.PE_ANALYTICS)
                    .date(OffsetDateTime.now()).build();
    promoCodeHistoryRepository.save(promoCodesHistory);
    logger.info("Perks promo code created with Id {}", savedPerksPromoCode.getUuid());

    return savedPerksPromoCode;
  }

  @Override
  public PromoCode createDTCPromoCode(final OfferFulfillmentEvent event) {
    final String customerNumber = event.getCustomerNumber();
    final String OpCoNumber = event.getOpcoNumber();

    Optional<CustomerInfo> optionalCustomerInfoResult =
            customerInfoRepository.findByCustomerIdAndOpcoId(customerNumber, OpCoNumber);
    final CustomerInfo customerInfoResult;

    if (optionalCustomerInfoResult.isEmpty()) {
      customerInfoResult = customerInfoRepository.save(
              CustomerInfo.builder().customerId(event.getCustomerNumber()).opcoId(event.getOpcoNumber()).build());
    } else {
      customerInfoResult = optionalCustomerInfoResult.get();
    }

    RewardSourceInfo dtcRewardSource = rewardSourceInfoRepository.getById("1");

    PromoCode dtcPromoCode =
            PromoCode.builder().uuid(PromoCodeUtil.getPromoCodeUUID())
                    .code(PromoCodeUtil.getDTCPromoCode(event.getRewardValue()))
                    .name(event.getOfferName())
                    .description(PromoCodeUtil.getDTCDescription(event.getRewardValue())).reward(event.getRewardValue())
                    .rewardCurrency("USD")
                    .minimumBucketSize(event.getRewardValue())
                    .promoCodeStatus(PromoCodeStatus.ACTIVE).codeStartDate(OffsetDateTime.now())
                    .codeEndDate(OffsetDateTime.now().plusMonths(6)
                            .withHour(23).withMinute(59).withSecond(59))
                    .lastModifiedDate(OffsetDateTime.now())
                    .customerInfo(customerInfoResult).rewardSourceInfo(dtcRewardSource)
                    .generatedDate(OffsetDateTime.now())
                    .modifiedSource(ModifiedSource.PE_ANALYTICS)
                    .build();

    dtcPromoCode = promoCodeRepository.save(dtcPromoCode);

    PromoCodeHistory promoCodesHistory =
            PromoCodeHistory.builder().promoCodeStatus(PromoCodeStatus.ACTIVE).promoCode(dtcPromoCode)
                    .date(OffsetDateTime.now()).modifiedSource(ModifiedSource.PE_ANALYTICS).build();
    promoCodeHistoryRepository.save(promoCodesHistory);

    logger.info("DTC promo code created with Id {}", dtcPromoCode.getUuid());

    return dtcPromoCode;

  }

  @Override
  public List<PromoCode> createBatchPerksPromoCodes(final List<Enrollment> enrollments) {

    final Set<Long> customerIdSet = promoCodeRepository.findByCode(getCurrentPerksPromoCode()).stream()
            .map(promoCode -> promoCode.getCustomerInfo().getId()).collect(
                    Collectors.toSet());

    final List<PromoCode> promoCodes =
            enrollments.stream().filter(enrollment -> !customerIdSet.contains(enrollment.getCustomerInfo().getId()))
                    .map(enrollment -> getPerksPromoCode(enrollment.getCustomerInfo())).toList();

    final List<PromoCode> savedPromoCodes = promoCodeRepository.saveAll(promoCodes);

    promoCodeHistoryRepository.saveAll(savedPromoCodes.stream()
            .map(promoCode -> PromoCodeHistory.builder().promoCode(promoCode)
                    .promoCodeStatus(promoCode.getPromoCodeStatus())
                    .modifiedSource(promoCode.getModifiedSource())
                    .date(OffsetDateTime.now()).build()).toList());

    logger.info("Created next batch of perks promo codes");

    return savedPromoCodes;
  }

  @Override
  public void processPromoCodesChanges(final List<PromoCode> promoCodes) {
    List<PromoCode> savedPromoCodes = promoCodeRepository.saveAll(
            promoCodes.stream().map(this::process).filter(Optional::isPresent).map(Optional::get).toList());
    promoCodeHistoryRepository.saveAll(savedPromoCodes.stream()
            .map(promoCode -> PromoCodeHistory.builder().promoCodeStatus(promoCode.getPromoCodeStatus())
                    .promoCode(promoCode)
                    .modifiedSource(promoCode.getModifiedSource()).date(OffsetDateTime.now()).build()).toList());
    logger.info("A batch of promo codes processed successfully");
  }

  private Optional<PromoCode> process(final PromoCode promoCode) {
    PromoCode updatedPromoCode = null;
    if (promoCode.getPromoCodeStatus() == PromoCodeStatus.INIT &&
            promoCode.getCodeStartDate().toLocalDate().isBefore(LocalDate.now())) {
      promoCode.setPromoCodeStatus(PromoCodeStatus.ACTIVE);
      promoCode.setLastModifiedDate(OffsetDateTime.now());
      promoCode.setModifiedSource(ModifiedSource.PE_ANALYTICS);
      logger.info("PromoCode wih uuid {} is set to ACTIVE", promoCode.getUuid());
      updatedPromoCode = promoCode;
    } else if (promoCode.getCodeEndDate().toLocalDate().equals(LocalDate.now()) ||
            promoCode.getCodeEndDate().toLocalDate().isBefore(LocalDate.now())) {
      promoCode.setPromoCodeStatus(PromoCodeStatus.EXPIRED);
      promoCode.setLastModifiedDate(OffsetDateTime.now());
      promoCode.setModifiedSource(ModifiedSource.PE_ANALYTICS);
      logger.info("PromoCode wih uuid {} is set to EXPIRED", promoCode.getUuid());
      updatedPromoCode = promoCode;
    }
    return Optional.ofNullable(updatedPromoCode);
  }

}
