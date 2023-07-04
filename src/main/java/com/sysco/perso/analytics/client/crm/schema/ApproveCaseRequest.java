package com.sysco.perso.analytics.client.crm.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class ApproveCaseRequest {
  @JsonProperty("type")
  private String type;

  @JsonProperty("Approval_status__c")
  private String status;
}
