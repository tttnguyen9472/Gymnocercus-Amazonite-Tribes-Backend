package com.greenfoxacademy.springwebapp.models.kingdoms;

import com.greenfoxacademy.springwebapp.models.buildings.Building;
import com.greenfoxacademy.springwebapp.models.resources.Resource;
import com.greenfoxacademy.springwebapp.models.troops.Troop;
import com.greenfoxacademy.springwebapp.models.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Kingdom {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  @OneToMany(mappedBy = "kingdom", cascade = CascadeType.ALL)
  private List<Resource> resources;
  @OneToMany(mappedBy = "kingdom", cascade = CascadeType.ALL)
  private List<Troop> troops;
  @OneToMany(mappedBy = "kingdom", cascade = CascadeType.ALL)
  private List<Building> buildings;
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumns({
      @JoinColumn(name = "location_x", referencedColumnName = "x"),
      @JoinColumn(name = "location_y", referencedColumnName = "y")
  })
  private Location location;
  @OneToOne(mappedBy = "kingdom", cascade = CascadeType.ALL)
  private User user;

  public Kingdom(String name, Location location) {
    this.name = name;
    this.location = location;
    resources = new ArrayList<>();
    troops = new ArrayList<>();
    buildings = new ArrayList<>();
  }

  public Kingdom(Long id, String name, Location location) {
    this(name, location);
    this.id = id;
  }

  public void addResource(Resource resource) {
    resources.add(resource);
  }

  public void addTroop(Troop troop) {
    troops.add(troop);
  }

}