package com.greenfoxacademy.springwebapp.models.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String username;
  @JsonIgnore
  private String password;
  @OneToOne
  @JoinColumn(name = "kingdom_id")
  private Kingdom kingdom;

  public User(String username) {
    this.username = username;
  }

  public User(String username, Kingdom kingdom) {
    this.username = username;
    this.kingdom = kingdom;
  }

  public User(String username, Kingdom kingdom, String password) {
    this.username = username;
    this.password = password;
    this.kingdom = kingdom;
  }
}