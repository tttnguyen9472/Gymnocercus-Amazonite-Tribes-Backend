package com.greenfoxacademy.springwebapp.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
public class AuthenticationRequest implements Serializable {

  private String username;
  private String password;

  public AuthenticationRequest(String username, String password) {
    this.setUsername(username);
    this.setPassword(password);
  }

}