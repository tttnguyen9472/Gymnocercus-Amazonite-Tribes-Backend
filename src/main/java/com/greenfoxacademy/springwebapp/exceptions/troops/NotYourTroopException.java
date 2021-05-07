package com.greenfoxacademy.springwebapp.exceptions.troops;

public class NotYourTroopException extends TroopException {
  private Long troopId;

  public NotYourTroopException(Long troopId) {
    this.troopId = troopId;
  }

  public NotYourTroopException() {
  }

  public Long getTroopId() {
    return troopId;
  }
}
