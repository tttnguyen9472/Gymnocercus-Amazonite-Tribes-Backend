package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.exceptions.kingdoms.KingdomException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughResourceException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotYourBuildingException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.TownHallLevelException;
import com.greenfoxacademy.springwebapp.models.buildings.Building;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingDTO;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingFactory;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingType;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.resources.Resource;
import com.greenfoxacademy.springwebapp.models.resources.ResourceType;
import com.greenfoxacademy.springwebapp.models.users.User;
import com.greenfoxacademy.springwebapp.repositories.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BuildingService {

  private final BuildingRepository buildingRepository;
  private final BuildingFactory buildingFactory;
  private final ResourceService resourceService;

  @Autowired
  public BuildingService(BuildingRepository buildingRepository, BuildingFactory buildingFactory,
                         ResourceService resourceService) {
    this.buildingRepository = buildingRepository;
    this.buildingFactory = buildingFactory;
    this.resourceService = resourceService;
  }


  public Building createBuilding(BuildingType buildingType) {
    return buildingFactory.createBuilding(buildingType);
  }


  public List<BuildingDTO> buildingToDTO(List<Building> buildingList) {

    return buildingList.stream()
        .map(
            BuildingDTO::new)
        .collect(Collectors.toList());
  }


  public BuildingDTO convertToDto(Building building) {
    return new BuildingDTO(building);
  }


  public Building saveBuilding(Building building) {
    return buildingRepository.save(building);
  }

  public List<Building> getBuildingsByKingdom_Id(Long id) {
    return buildingRepository.findBuildingsByKingdom_Id(id);
  }

  public BuildingDTO updateBuilding(User user, Kingdom kingdom, Long buildingId)
      throws KingdomException, NotEnoughResourceException {
    Building foundBuilding;

    foundBuilding = findBuildingById(buildingId);

    isBuildingBelongToTheUser(buildingId, kingdom);

    Resource gold = resourceService.updateResourceOfKingdom(kingdom, ResourceType.GOLD);

    isTownhallLevelGreater(kingdom, foundBuilding);

    isGoldEnough(foundBuilding, gold);

    foundBuilding.setLevel(foundBuilding.getLevel() + 1);
    gold.setAmount(gold.getAmount() - foundBuilding.getLevel() * 100);
    buildingRepository.save(foundBuilding);
    resourceService.updateResource(gold);
    resourceService.updateResourceGeneration(kingdom, foundBuilding);

    return convertToDto(foundBuilding);
  }

  private void isGoldEnough(Building foundBuilding, Resource gold) throws NotEnoughResourceException {
    if ((foundBuilding.getLevel() + 1) * 100
        >
        gold.getAmount()) {
      throw new NotEnoughResourceException();
    }
  }

  private void isTownhallLevelGreater(Kingdom kingdom, Building foundBuilding)
      throws TownHallLevelException, NotYourBuildingException {
    if (!foundBuilding.getType().equals("TownHall")) {
      Integer townhallLevel = getBuildingFromList(kingdom, BuildingType.TOWNHALL).getLevel();
      if (townhallLevel <= foundBuilding.getLevel()) {
        throw new TownHallLevelException(townhallLevel);
      }
    }
  }

  private void isBuildingBelongToTheUser(Long buildingId, Kingdom kingdom) throws NotYourBuildingException {
    if (!buildingRepository.findById(buildingId).get().getKingdom().getId().equals(kingdom.getId())) {
      throw new NotYourBuildingException(buildingId.toString());
    }
  }

  private Building findBuildingById(Long id) throws NotYourBuildingException {
    if (!buildingRepository.findById(id).isPresent()) {
      throw new NotYourBuildingException(id.toString());
    }
    return buildingRepository.findById(id).get();
  }


  public Building getBuildingFromList(Kingdom kingdom, BuildingType buildingType) throws NotYourBuildingException {
    Optional<Building> optionalBuilding = buildingRepository.findBuildingsByKingdom_Id(kingdom.getId()).stream()
        .filter(building -> building.getType().equals(buildingType.toString()))
        .findFirst();
    return optionalBuilding.orElseThrow(NotYourBuildingException::new);
  }


  public Building renderBuildingToKingdom(Building building, Kingdom kingdom) {
    building.setKingdom(kingdom);
    return building;
  }

  public void initializeBuildings(User user) {
    saveBuilding(
        renderBuildingToKingdom(createBuilding(BuildingType.TOWNHALL), user.getKingdom()));
    saveBuilding(renderBuildingToKingdom(createBuilding(BuildingType.FARM), user.getKingdom()));
    saveBuilding(renderBuildingToKingdom(createBuilding(BuildingType.MINE), user.getKingdom()));
    saveBuilding(renderBuildingToKingdom(createBuilding(BuildingType.ACADEMY), user.getKingdom()));
  }

}