package com.greenfoxacademy.springwebapp.exceptions.kingdoms;

public class ReservedKingdomnameException extends Exception {
  private String requestedName;

  public ReservedKingdomnameException(String requestedName) {
    this.requestedName = requestedName;
  }

  public String getRequestedName() {
    return requestedName;
  }
}
