package com.greenfoxacademy.springwebapp.models.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
  private String username;
  private String password;
  private String kingdom;

  public RegisterRequestDTO(String username) {
    this.username = username;
  }

}
