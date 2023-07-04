package com.sysco.perso.analytics.client.crm.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class Case {
  @JsonProperty("Account")
  private Account account;

  @JsonProperty("RecordTypeId")
  private String recordTypeId;

  @JsonProperty("OwnerId")
  private String ownerID;

  @JsonProperty("Subject")
  private String subject;

  @JsonProperty("Description")
  private String description;

  @JsonProperty("Request_Type_for_Billing_Adjustment__c")
  private String requestType;

  @JsonProperty("Reason_Code_for_Billing_Adjustment__c")
  private String reasonCode;

}
