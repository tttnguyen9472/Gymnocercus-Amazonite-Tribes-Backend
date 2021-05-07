package com.greenfoxacademy.springwebapp.models.troops;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TroopDTO {
  private Long id;
  private Integer level;
  private Integer hp;
  private Integer attack;
  private Integer defense;
  private Timestamp startedAt;
  private Timestamp finishedAt;
  @JsonProperty("kingdom_id")
  private Long kingdomId;

  public TroopDTO(Troop troop) {
    this.id = troop.getId();
    this.level = troop.getLevel();
    this.hp = troop.getHp();
    this.attack = troop.getAttack();
    this.defense = troop.getDefense();
    this.startedAt = troop.getStartedAt();
    this.finishedAt = troop.getFinishedAt();
  }

}
