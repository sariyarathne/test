package com.sysco.perso.analytics.entity.enums;

public enum EnrollmentType {
  FREE_TRIAL("Free Trail"),
  ENROLLED("Enrolled"),
  UN_ENROLLED("Un Enrolled");

  private String value;

  EnrollmentType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
