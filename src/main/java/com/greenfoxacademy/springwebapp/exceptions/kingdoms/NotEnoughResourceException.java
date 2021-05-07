package com.greenfoxacademy.springwebapp.exceptions.kingdoms;


public class NotEnoughResourceException extends Exception {
  private String message;

  public NotEnoughResourceException(String message) {
    this.message = message;
  }

  public NotEnoughResourceException() {
    message = "Insufficient resources.";
  }

  public String getMessage() {
    return message;
  }
}
