package com.greenfoxacademy.springwebapp.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionInformationDTO {

  private String status;
  private String message;

  public TransactionInformationDTO(String message) {
    this.message = message;
  }
}
