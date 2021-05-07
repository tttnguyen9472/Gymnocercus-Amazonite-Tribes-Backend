package com.greenfoxacademy.springwebapp.models.kingdoms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarResultDTO {

  private String status;
  private Integer gold;
  private Integer food;
  @JsonProperty(value = "troops_lost")
  private Integer troopsLost;
  @JsonProperty(value = "troops_killed")
  private Integer troopsKilled;

  public WarResultDTO(String status, Integer troopsLost, Integer troopsKilled) {
    this.status = status;
    this.troopsLost = troopsLost;
    this.troopsKilled = troopsKilled;
  }
}
