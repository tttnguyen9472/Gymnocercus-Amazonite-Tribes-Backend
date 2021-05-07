package com.greenfoxacademy.springwebapp.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ErrorEntityDTO {
  @JsonProperty(value = "status_code")
  private Integer statusCode;
  private HttpStatus status;
  private String message;

  public ErrorEntityDTO(HttpStatus status, String message) {
    this.statusCode = HttpStatus.UNAUTHORIZED.value();
    this.status = status;
    this.message = message;
  }
}
