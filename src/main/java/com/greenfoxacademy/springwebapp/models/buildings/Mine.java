package com.greenfoxacademy.springwebapp.models.buildings;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Mine extends Building {
  public Mine() {
    super();
  }

  @Override
  public String getType() {
    return "Mine";
  }

}
