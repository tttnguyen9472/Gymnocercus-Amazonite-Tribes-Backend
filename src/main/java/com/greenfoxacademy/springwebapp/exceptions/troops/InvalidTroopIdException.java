package com.greenfoxacademy.springwebapp.exceptions.troops;

public class InvalidTroopIdException extends TroopException {
  private Long troopId;

  public InvalidTroopIdException(Long troopId) {
    this.troopId = troopId;
  }

  public InvalidTroopIdException() {
  }

  public Long getTroopId() {
    return troopId;
  }
}
