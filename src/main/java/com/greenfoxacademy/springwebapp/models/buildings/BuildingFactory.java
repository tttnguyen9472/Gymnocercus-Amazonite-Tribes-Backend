package com.greenfoxacademy.springwebapp.models.buildings;

import org.springframework.stereotype.Service;

@Service
public class BuildingFactory {

  public Building createBuilding(BuildingType buildingType) {
    if (buildingType.equals(BuildingType.TOWNHALL)) {
      return new TownHall();
    } else if (buildingType.equals(BuildingType.FARM)) {
      return new Farm();
    } else if (buildingType.equals(BuildingType.MINE)) {
      return new Mine();
    } else if (buildingType.equals(BuildingType.ACADEMY)) {
      return new Academy();
    }
    return null;
  }
}