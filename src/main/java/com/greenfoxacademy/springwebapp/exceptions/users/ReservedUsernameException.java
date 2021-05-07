package com.greenfoxacademy.springwebapp.exceptions.users;

public class ReservedUsernameException extends UserException {
  private String requestedName;

  public ReservedUsernameException(String requestedName) {
    this.requestedName = requestedName;
  }

  public ReservedUsernameException() {
  }

  public String getRequestedName() {
    return requestedName;
  }
}
