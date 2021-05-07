package com.greenfoxacademy.springwebapp.models.kingdoms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class LocationId implements Serializable {

  private Integer x;
  private Integer y;

  public LocationId(Integer x, Integer y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LocationId)) {
      return false;
    }
    LocationId that = (LocationId) o;
    return getX().equals(that.getX())
        && getY().equals(that.getY());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getX(), getY());
  }
}
