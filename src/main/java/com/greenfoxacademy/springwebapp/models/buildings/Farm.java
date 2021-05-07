package com.greenfoxacademy.springwebapp.models.buildings;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Farm extends Building {

  public Farm() {
    super();
  }

  @Override
  public String getType() {
    return "Farm";
  }

}
