package com.greenfoxacademy.springwebapp.models.buildings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor
public class BuildingDTO {
  private String type;
  private Long id;
  private Integer level;
  private Timestamp startedAt;
  private Timestamp finishedAt;

  public BuildingDTO(Building building) {
    this.type = building.getType();
    this.id = building.getId();
    this.level = building.getLevel();
    this.startedAt = building.getStartedAt();
    this.finishedAt = building.getFinishedAt();
  }

}

