package com.sysco.perso.analytics.service.impl;

import com.sysco.perso.analytics.client.statemachine.OfferTrackerClient;
import com.sysco.perso.analytics.client.statemachine.data.TrackerData;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.PerksTrackingData;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.entity.enums.TrackingState;
import com.sysco.perso.analytics.repository.PerksTrackingDataRepository;
import com.sysco.perso.analytics.service.OfferTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Transactional
public class OfferTrackerServiceImpl implements OfferTrackerService {
  private final OfferTrackerClient offerTrackerClient;
  private final PerksTrackingDataRepository perksTrackingDataRepository;

  @Autowired
  OfferTrackerServiceImpl(final OfferTrackerClient offerTrackerClient,
                          final PerksTrackingDataRepository perksTrackingDataRepository) {
    this.offerTrackerClient = offerTrackerClient;
    this.perksTrackingDataRepository = perksTrackingDataRepository;
  }

  @Override
  public void processEnrollmentChanges(final List<Enrollment> enrollments) {
    final LocalDate offerStartDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    final LocalDate offerEndDate = offerStartDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

    final List<Enrollment> filteredEnrollments = enrollments.stream().filter(this::isNewEnrollment).toList();

    List<TrackerData> objects = filteredEnrollments.stream()
            .map(enrollment -> TrackerData.builder().customerNumber(enrollment.getCustomerInfo().getCustomerId())
                    .opcoId(enrollment.getCustomerInfo().getOpcoId()).drawdownPeriod(6)
                    .hurdleLevel(7500).offerName("Perks Fee Waiver | Spend").offerSource("pfw").rewardValue(894)
                    .sampleGroup("test")
                    .offerStartDate(offerStartDate).offerEndDate(offerEndDate).build())
            .toList();
    final boolean trackerSuccess = this.offerTrackerClient.startTracker(objects);

    if (trackerSuccess) {
      perksTrackingDataRepository.saveAll(filteredEnrollments.stream()
              .map(enrollment -> toPerksTrackingData(enrollment, offerStartDate, offerEndDate)).toList());
    }
  }

  private boolean isNewEnrollment(final Enrollment enrollment) {
    return enrollment.getPerksTrackingDataSet().isEmpty() &&
            enrollment.getEnrollmentType() == EnrollmentType.FREE_TRIAL;
  }

  private PerksTrackingData toPerksTrackingData(Enrollment enrollment, LocalDate offerStartDate,
                                                LocalDate offerEndDate) {
    return PerksTrackingData.builder().enrollment(enrollment).startDate(offerStartDate).endDate(offerEndDate)
            .trackingState(TrackingState.IN_PROGRESS).createdDate(OffsetDateTime.now())
            .build();
  }

}
