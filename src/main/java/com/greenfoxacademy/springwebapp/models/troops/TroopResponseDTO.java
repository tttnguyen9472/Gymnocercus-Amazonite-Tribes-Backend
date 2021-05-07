package com.greenfoxacademy.springwebapp.models.troops;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TroopResponseDTO {
  private Long id;
  private Integer level;
  private Integer hp;
  private Integer attack;
  private Integer defense;
  @JsonProperty("kingdom_id")
  private Long kingdomId;
  private String message;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TroopResponseDTO)) {
      return false;
    }
    TroopResponseDTO that = (TroopResponseDTO) o;
    return Objects.equals(getId(), that.getId())
        && Objects.equals(getLevel(), that.getLevel())
        && Objects.equals(getHp(), that.getHp())
        && Objects.equals(getAttack(), that.getAttack())
        && Objects.equals(getDefense(), that.getDefense())
        && Objects.equals(getKingdomId(), that.getKingdomId())
        && Objects.equals(getMessage(), that.getMessage());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getLevel(), getHp(), getAttack(), getDefense(), getKingdomId(), getMessage());
  }
}
