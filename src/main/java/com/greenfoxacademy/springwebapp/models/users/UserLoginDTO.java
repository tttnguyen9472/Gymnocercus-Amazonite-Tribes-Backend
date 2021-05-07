package com.greenfoxacademy.springwebapp.models.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.greenfoxacademy.springwebapp.security.AuthenticationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginDTO {

  private String status;
  private String error;
  private String token;

  public UserLoginDTO(AuthenticationResponse authenticationResponse) {
    this.status = "ok";
    this.token = authenticationResponse.getJwt();
  }

  public UserLoginDTO(String error) {
    this.status = "error";
    this.error = error;
  }
}
