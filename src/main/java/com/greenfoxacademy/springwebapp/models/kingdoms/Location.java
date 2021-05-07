package com.greenfoxacademy.springwebapp.models.kingdoms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;

@Getter
@Setter
@NoArgsConstructor
@Entity
@IdClass(LocationId.class)
public class Location {

  @Id
  private Integer x;
  @Id
  private Integer y;
  @OneToOne(mappedBy = "location")
  private Kingdom kingdom;

  public Location(Integer x, Integer y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Location)) {
      return false;
    }
    Location location = (Location) o;
    return getX().equals(location.getX())
        && getY().equals(location.getY());
  }

  @Override
  public String toString() {
    return "[x=" + x + "; y=" + y + "]";
  }
}
