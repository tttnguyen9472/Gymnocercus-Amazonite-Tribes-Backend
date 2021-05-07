package com.greenfoxacademy.springwebapp.exceptions.kingdoms;

public class NotYourBuildingException extends KingdomException {
  private String buildingId;

  public NotYourBuildingException(String buildingId) {
    this.buildingId = buildingId;
  }

  public NotYourBuildingException() {
  }

  public String getBuildingId() {
    return buildingId;
  }
}
