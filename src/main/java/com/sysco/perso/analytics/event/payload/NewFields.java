package com.sysco.perso.analytics.event.payload;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class NewFields {

  @JsonProperty("Id")
  private String id = "";

  @JsonProperty("Name")
  private String name = "";

  @JsonProperty("Account_ID__c")
  private String accountId = "";

  @JsonProperty("Status__c")
  private String status = "";

  @JsonProperty("Sysco_Perks__c")
  private String syscoPerks = "";

  @JsonProperty("Perks_Enrollment_Date__c")
  private String perksEnrollmentDate = "";

}
