package com.sysco.perso.analytics.client.crm.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class Account {
  @JsonProperty("Account_ID__c")
  private String accountId;
}
