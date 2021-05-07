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
public class ScoutResponseDTO {
  private LocationDTO location;
  private Long kingdom_id;
  private String kingdom_name;
  private String troop_number;

  public ScoutResponseDTO(LocationDTO location, Long kingdomId, String kingdomName) {
    this.location = location;
    this.kingdom_id = kingdomId;
    this.kingdom_name = kingdomName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ScoutResponseDTO)) {
      return false;
    }
    ScoutResponseDTO that = (ScoutResponseDTO) o;
    return Objects.equals(getLocation(), that.getLocation())
        && Objects.equals(getKingdom_id(), that.getKingdom_id())
        && Objects.equals(getKingdom_name(), that.getKingdom_name())
        && Objects.equals(getTroop_number(), that.getTroop_number());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getLocation(), getKingdom_id(), getKingdom_name(), getTroop_number());
  }
}
