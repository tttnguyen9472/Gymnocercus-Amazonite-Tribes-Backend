package com.greenfoxacademy.springwebapp.exceptions.kingdoms;

public class TownHallLevelException extends KingdomException {
  private Integer townhallLevel;

  public TownHallLevelException(Integer townhallLevel) {
    this.townhallLevel = townhallLevel;
  }

  public TownHallLevelException() {
  }

  public Integer getTownhallLevel() {
    return townhallLevel;
  }
}
