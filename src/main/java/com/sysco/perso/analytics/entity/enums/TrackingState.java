package com.sysco.perso.analytics.entity.enums;

public enum TrackingState {
  IN_PROGRESS("In Progress"),
  COMPLETED("Completed"),
  UN_COMPLETED("Un Completed");

  private String value;

  private TrackingState(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
