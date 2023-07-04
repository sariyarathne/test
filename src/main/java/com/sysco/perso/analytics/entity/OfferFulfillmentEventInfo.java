package com.sysco.perso.analytics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "offer_fulfillment_event_info")
public class OfferFulfillmentEventInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "offer_fulfillment_event_sequence_generator")
  @SequenceGenerator(name = "offer_fulfillment_event_sequence_generator",
          sequenceName = "offer_fulfillment_event_sequence", initialValue = 1,
          allocationSize = 1)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "offer_id", nullable = false, length = 32)
  private String offerId;

  @Column(name = "campaign_id", nullable = false)
  private Integer campaignId;

  @Column(name = "campaign_name", nullable = false, length = 32)
  private String campaignName;

  @Column(name = "customer_number", nullable = false, length = 32)
  private String customerNumber;

  @Column(name = "opco_number", nullable = false, length = 32)
  private String opcoNumber;

  @Column(name = "reward_source", nullable = false, length = 32)
  private String rewardSource;

  @Column(name = "offer_name", nullable = false, length = 32)
  private String offerName;

  @Column(name = "reward_value", nullable = false, length = 32)
  private float rewardValue;

  @Column(name = "offer_description", nullable = false, length = 32)
  private String offerDescription;

  @Column(name = "offer_end_date", nullable = false, length = 32)
  private String offerEndDate;

  @Column(name = "success", nullable = false, length = 32)
  private boolean isSuccess;

  @OneToOne(targetEntity = PromoCode.class)
  @JoinColumn(name = "uuid")
  private PromoCode promoCode;

}




