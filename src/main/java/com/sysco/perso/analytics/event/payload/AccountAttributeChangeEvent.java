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
public class AccountAttributeChangeEvent {
  @JsonProperty("ChangeEventHeader")
  private ChangeEventHeader changeEventHeader = new ChangeEventHeader();

  @JsonProperty("NewFields")
  private NewFields newFields = new NewFields();

  @JsonProperty("OldFields")
  OldFields oldFields = new OldFields();
}
