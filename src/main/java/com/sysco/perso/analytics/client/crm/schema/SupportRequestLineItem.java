package com.sysco.perso.analytics.client.crm.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class SupportRequestLineItem {

  @JsonProperty("Support_Request_Number__c")
  private String supportRequestNumber;

  @JsonProperty("Product__r")
  private Product product;

  @JsonProperty("Item_Amount__c")
  private double itemAmount;

  @JsonProperty("Quantity_Needed__c")
  private int quantity;
}
