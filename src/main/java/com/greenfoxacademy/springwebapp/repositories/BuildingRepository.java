package com.greenfoxacademy.springwebapp.repositories;

import com.greenfoxacademy.springwebapp.models.buildings.Building;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends CrudRepository<Building, Long> {
  List<Building> findBuildingsByKingdom_Id(Long kingdomId);
}

