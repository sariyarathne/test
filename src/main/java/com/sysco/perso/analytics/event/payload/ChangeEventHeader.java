package com.sysco.perso.analytics.event.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ChangeEventHeader {

  @JsonProperty("entityName")
  private String entityName = "";

  @JsonProperty("changeType")
  private String changeType = "";

  @JsonProperty("changedFields")
  private List<String> changedFields = Collections.emptyList();

  @JsonProperty("recordIds")
  private List<String> recordIds = Collections.emptyList();
}
