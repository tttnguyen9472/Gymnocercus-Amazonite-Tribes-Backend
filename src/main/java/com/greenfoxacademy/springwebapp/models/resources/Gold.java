package com.greenfoxacademy.springwebapp.models.resources;

import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Gold extends Resource {

  public Gold(Integer amount, Integer generate) {
    super(amount, generate);
  }

  public Gold(Kingdom kingdom, Long id, Integer amount, Integer generation, Timestamp lastUpdated) {
    super(kingdom, id, amount, generation, lastUpdated);
  }

  @Override
  public String getType() {
    return "Gold";
  }

}
