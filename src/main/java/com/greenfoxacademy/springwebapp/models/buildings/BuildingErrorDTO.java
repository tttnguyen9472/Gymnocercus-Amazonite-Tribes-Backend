package com.greenfoxacademy.springwebapp.models.buildings;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildingErrorDTO {

  private String message;

  public BuildingErrorDTO(String error) {
    this.message = error;
  }
}