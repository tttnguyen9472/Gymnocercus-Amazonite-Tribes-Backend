package com.greenfoxacademy.springwebapp.models.buildings;

public enum BuildingType {
  TOWNHALL {
    public String toString() {
      return "TownHall";
    }
  },
  FARM {
    public String toString() {
      return "Farm";
    }
  },
  MINE {
    public String toString() {
      return "Mine";
    }
  },
  ACADEMY {
    public String toString() {
      return "Academy";
    }
  }
}


