package com.greenfoxacademy.springwebapp.models.resources;

import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
public abstract class Resource {

  @ManyToOne(targetEntity = Kingdom.class)
  @JoinColumn(name = "kingdom_id")
  protected Kingdom kingdom;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Integer amount;
  private Integer generation;
  @Column(name = "last_updated")
  private Timestamp lastUpdated;

  public Resource(Integer amount, Integer generation) {
    this.amount = amount;
    this.generation = generation;
    lastUpdated = new Timestamp(System.currentTimeMillis());
  }

  public abstract String getType();

}
