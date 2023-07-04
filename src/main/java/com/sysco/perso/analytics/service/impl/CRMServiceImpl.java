package com.sysco.perso.analytics.service.impl;

import com.sysco.perso.analytics.client.crm.CRMClient;
import com.sysco.perso.analytics.client.crm.schema.CRMGenericResponse;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.SupportRequest;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.repository.SupportRequestRepository;
import com.sysco.perso.analytics.service.CRMService;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CRMServiceImpl implements CRMService {

  private static final Logger logger = LoggerFactory.getLogger(CRMServiceImpl.class);

  private final CRMClient crmClient;

  private final SupportRequestRepository supportRequestRepository;

  @SuppressWarnings("java:S3252")
  private final Sinks.Many<Enrollment> sink = Sinks.many().unicast().onBackpressureBuffer();

  @Autowired
  public CRMServiceImpl(final CRMClient crmClient, final SupportRequestRepository supportRequestRepository) {
    this.crmClient = crmClient;
    this.supportRequestRepository = supportRequestRepository;

    sink.asFlux().limitRate(200, 0).publishOn(Schedulers.boundedElastic()).flatMap(this::process)
            .map(this::toSupportRequest)
            .filter(list -> !list.isEmpty()).subscribe(this::saveSupportRequests);
  }

  private void saveSupportRequests(final List<SupportRequest> supportRequests) {
    List<SupportRequest> saved = this.supportRequestRepository.saveAll(supportRequests);
    saved.forEach(s ->
            logger.info("Created Support request {} for customer {}", s.getSrReference(),
                    s.getEnrollment().getCustomerInfo().getId())
    );
  }

  @Override
  public void processMembershipFee(final List<Enrollment> enrollments) {
    final Set<Long> customerIdSet =
            supportRequestRepository.findBySrCreatedDateBetween(
                            LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()),
                            LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())).stream()
                    .map(supportRequest -> supportRequest.getEnrollment().getCustomerInfo().getId()).collect(
                            Collectors.toSet());

    enrollments.stream().filter(enrollment -> !customerIdSet.contains(enrollment.getCustomerInfo().getId()))
            .forEach(sink::tryEmitNext);

  }

  private List<SupportRequest> toSupportRequest(final List<CompositeResponse> compositeResponseList) {
    return compositeResponseList.stream()
            .map(compositeResponse -> SupportRequest.builder().enrollment(compositeResponse.enrollment)
                    .srReference(compositeResponse.genericResponse.getId()).srCreatedDate(
                            LocalDate.now()).build())
            .toList();
  }

  private Mono<List<CompositeResponse>> process(final Enrollment enrollment) {
    String customerNumber =
            enrollment.getCustomerInfo().getOpcoId() + "-" + enrollment.getCustomerInfo().getCustomerId();
    Mono<List<CRMGenericResponse>> crmGenericResponseMono = Mono.empty();

    if (isTrialEnrolment(enrollment) && !isMilestoneMonth(enrollment)) {
      crmGenericResponseMono = this.crmClient.waiveOffPerksMembershipFee(customerNumber);
    }

    return crmGenericResponseMono
            .map(crmGenericResponses -> crmGenericResponses.stream()
                    .map(crmGenericResponse -> CompositeResponse.builder().genericResponse(crmGenericResponse)
                            .enrollment(enrollment).build())
                    .toList());
  }

  private boolean isMilestoneMonth(final Enrollment enrollment) {
    return enrollment.getFreeTrialEndDate().getMonth().equals(LocalDate.now().getMonth());
  }

  private boolean isTrialEnrolment(final Enrollment enrollment) {
    return EnrollmentType.FREE_TRIAL == enrollment.getEnrollmentType();
  }

  @Builder
  @Getter
  private static class CompositeResponse {
    private CRMGenericResponse genericResponse;
    private Enrollment enrollment;
  }

}
