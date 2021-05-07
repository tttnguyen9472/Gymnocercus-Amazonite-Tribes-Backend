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
public class Food extends Resource {

  public Food(Integer amount, Integer generate) {
    super(amount, generate);
  }

  public Food(Kingdom kingdom, Long id, Integer amount, Integer generation, Timestamp lastUpdated) {
    super(kingdom, id, amount, generation, lastUpdated);
  }

  @Override
  public String getType() {
    return "Food";
  }

}
