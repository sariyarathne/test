package com.sysco.perso.analytics.event.payload;

import java.util.Arrays;
import java.util.List;

public enum PerksEnrollType {
  ENROLLED("Enrolled"),
  ENROLLED_FREE_TRIAL("Enrolled free trial");

  private final String value;

  PerksEnrollType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static List<String> getValues() {
    return Arrays.stream(PerksEnrollType.values()).map(PerksEnrollType::getValue).toList();
  }
}
