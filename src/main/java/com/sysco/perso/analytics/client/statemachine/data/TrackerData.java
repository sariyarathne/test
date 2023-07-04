package com.sysco.perso.analytics.client.statemachine.data;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@EqualsAndHashCode
public class TrackerData {
  private String opcoId;
  private String customerNumber;
  private String offerName;
  private Integer hurdleLevel;
  private String frequency;
  private LocalDate offerStartDate;
  private LocalDate offerEndDate;
  private Integer rewardValue;
  private String offerSource;
  private String sampleGroup;
  private Integer drawdownPeriod;
}
