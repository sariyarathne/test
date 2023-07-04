package com.sysco.perso.analytics.event.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class OfferFulfillmentEvent {

  @JsonProperty("offer_id")
  private String offerId;

  @JsonProperty("campaign_id")
  private int campaignId;

  @JsonProperty("campaign_name")
  private String campaignName;

  @JsonProperty("customer_number")
  private String customerNumber;

  @JsonProperty("reward_source")
  private String rewardSource;

  @JsonProperty("opco_number")
  private String opcoNumber;

  @JsonProperty("offer_name")
  private String offerName;

  @JsonProperty("reward_value")
  private float rewardValue;

  @JsonProperty("offer_description")
  private String offerDescription;

  @JsonProperty("offer_end_date")
  private String offerEndDate;

  @JsonProperty("success")
  private boolean isSuccess;
}
