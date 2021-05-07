package com.greenfoxacademy.springwebapp.models.buildings;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class TownHall extends Building {
  public TownHall() {
    super();
  }

  @Override
  public String getType() {
    return "TownHall";
  }

}
