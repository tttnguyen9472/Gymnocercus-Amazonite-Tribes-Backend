package com.greenfoxacademy.springwebapp.models.buildings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuildingObjectDTO {
  private String dtype;
  private Long id;
  private Timestamp finishedAt;
  private Integer level;
  private Timestamp startedAt;
  private Long kingdomId;
}
