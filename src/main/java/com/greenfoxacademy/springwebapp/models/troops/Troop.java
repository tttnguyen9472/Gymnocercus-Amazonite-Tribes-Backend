package com.greenfoxacademy.springwebapp.models.troops;

import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Troop {

  @ManyToOne
  @JoinColumn(name = "kingdom_id")
  protected Kingdom kingdom;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Integer level;
  private Integer hp;
  private Integer attack;
  private Integer defense;
  @Column(name = "started_at")
  private Timestamp startedAt;
  @Column(name = "finished_at")
  private Timestamp finishedAt;


  public Troop() {
    this.level = 1;
    this.hp = 30;
    this.attack = 10;
    this.defense = 5;
  }

  public Troop(Kingdom kingdom) {
    this();
    this.kingdom = kingdom;
  }
}
