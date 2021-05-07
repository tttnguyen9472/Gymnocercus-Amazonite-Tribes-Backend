package com.greenfoxacademy.springwebapp.models.buildings;

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
@Entity
@AllArgsConstructor
public abstract class Building {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;
  protected Integer level;
  @Column(name = "started_at")
  protected Timestamp startedAt;
  @Column(name = "finished_at")
  protected Timestamp finishedAt;
  @ManyToOne
  @JoinColumn(name = "kingdom_id")
  protected Kingdom kingdom;

  public Building() {
    this.level = 1;
  }

  public abstract String getType();
}