package com.greenfoxacademy.springwebapp.models.buildings;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Academy extends Building {
  public Academy() {
    super();
  }

  @Override
  public String getType() {
    return "Academy";
  }

}
