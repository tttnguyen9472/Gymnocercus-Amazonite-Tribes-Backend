package com.greenfoxacademy.springwebapp.exceptions;

import io.jsonwebtoken.JwtException;

public class MissingHeaderParameterTokenException extends JwtException {
  public MissingHeaderParameterTokenException() {
    super("Missing token parameter in request header");
  }
}
