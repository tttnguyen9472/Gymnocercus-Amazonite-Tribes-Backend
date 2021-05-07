package com.greenfoxacademy.springwebapp.models.kingdoms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
  private Integer x;
  private Integer y;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LocationDTO)) {
      return false;
    }
    LocationDTO that = (LocationDTO) o;
    return Objects.equals(getX(), that.getX()) && Objects.equals(getY(), that.getY());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getX(), getY());
  }
}
