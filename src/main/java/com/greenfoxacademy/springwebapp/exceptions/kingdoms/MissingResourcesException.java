package com.greenfoxacademy.springwebapp.exceptions.kingdoms;

import java.util.List;

public class MissingResourcesException extends KingdomException {
  private final List<String> missingParameterList;

  public MissingResourcesException(List<String> missingParameterList) {
    this.missingParameterList = missingParameterList;
  }

  public List<String> getMissingParameterList() {
    return missingParameterList;
  }
}
