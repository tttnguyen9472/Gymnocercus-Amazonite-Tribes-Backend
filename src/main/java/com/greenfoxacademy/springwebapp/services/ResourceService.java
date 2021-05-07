package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.exceptions.kingdoms.MissingResourcesException;
import com.greenfoxacademy.springwebapp.models.buildings.Building;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.resources.Resource;
import com.greenfoxacademy.springwebapp.models.resources.ResourceDTO;
import com.greenfoxacademy.springwebapp.models.resources.ResourceFactory;
import com.greenfoxacademy.springwebapp.models.resources.ResourceType;
import com.greenfoxacademy.springwebapp.models.users.User;
import com.greenfoxacademy.springwebapp.repositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ResourceService {

  ResourceRepository resourceRepository;
  ResourceFactory resourceFactory;
  TimeService timeService;

  @Autowired
  public ResourceService(ResourceRepository resourceRepository, ResourceFactory resourceFactory,
                         TimeService timeService) {
    this.resourceRepository = resourceRepository;
    this.resourceFactory = resourceFactory;
    this.timeService = timeService;
  }

  public Resource createResource(ResourceType resourceType) {
    return resourceFactory.createResource(resourceType);
  }

  public Resource renderResourceToKingdom(Resource resource, Kingdom kingdom) {
    resource.setKingdom(kingdom);
    return resource;
  }

  public Resource saveResourceWithKingdomId(ResourceType resourceType, Kingdom kingdom) {
    Resource resource = createResource(resourceType);
    resource.setKingdom(kingdom);
    return resource;
  }

  public List<Resource> getActualResourcesOfKingdom(Kingdom kingdom) {
    updateResourceOfKingdom(kingdom, ResourceType.FOOD);
    updateResourceOfKingdom(kingdom, ResourceType.GOLD);
    return kingdom.getResources();
  }

  public Resource updateResourceOfKingdom(Kingdom kingdom, ResourceType resourceType) {
    Resource resource = getResourceFromList(kingdom, resourceType);
    Long minutesSinceLastUpdate = (timeService.minutesBetweenATimestampAndTheActualTime(resource.getLastUpdated()));
    resource.setAmount(resource.getAmount() + ((resource.getGeneration() + 5) * minutesSinceLastUpdate.intValue()));
    resource.setLastUpdated(new Timestamp(resource.getLastUpdated().getTime() + minutesSinceLastUpdate * 60000));
    return resourceRepository.save(resource);
  }

  public Resource getResourceFromList(Kingdom kingdom, ResourceType resourceType) {
    Optional<Resource> optionalResource = resourceRepository.findResourcesByKingdom_id(kingdom.getId()).stream()
        .filter(resource -> resource.getType().equals(resourceType.toString()))
        .findFirst();
    return optionalResource.orElseGet(() -> getNewResource(kingdom, resourceType));
  }

  private Resource getNewResource(Kingdom kingdom, ResourceType resourceType) {
    List<Resource> resourceList = kingdom.getResources();
    Resource newResource = resourceFactory.createResource(resourceType);
    newResource.setKingdom(kingdom);
    resourceList.add(resourceRepository.save(newResource));
    kingdom.setResources(resourceList);
    return newResource;
  }

  public List<ResourceDTO> resourceToDTO(List<Resource> resourceList) {

    return resourceList.stream()
        .map(ResourceDTO::new)
        .collect(Collectors.toList());
  }


  public Resource saveResource(Resource resource) {
    return resourceRepository.save(resource);
  }

  public List<Resource> getResourcesByKingdomId(Long id) {
    return resourceRepository.findResourcesByKingdom_id(id);
  }

  public void reduceResourceOfKingdom(Integer cost, Integer foodAmount, Kingdom kingdom) {
    List<Resource> resourceList = getActualResourcesOfKingdom(kingdom);
    reduceGold(cost, resourceList);
    reduceFoodGeneration(foodAmount, resourceList);
    kingdom.setResources(resourceList);
  }

  protected void reduceGold(Integer cost, List<Resource> resourceList) {
    resourceList.forEach(resource -> {
      if (resource.getType().equals(ResourceType.GOLD.toString())) {
        resource.setAmount(resource.getAmount() - cost);
      }
    });
  }

  protected void reduceFoodGeneration(Integer foodAmount, List<Resource> resourceList) {
    resourceList.forEach(resource -> {
      if (resource.getType().equals(ResourceType.FOOD.toString())) {
        resource.setGeneration(resource.getGeneration() - foodAmount);
      }
    });
  }

  protected void reduceFood(Integer foodAmount, List<Resource> resourceList) {
    resourceList.forEach(resource -> {
      if (resource.getType().equals(ResourceType.FOOD.toString())) {
        resource.setAmount(resource.getAmount() - foodAmount);
      }
    });
  }


  public Boolean hasEnoughGold(Integer cost, Kingdom kingdom) {
    Resource gold = updateResourceOfKingdom(kingdom, ResourceType.GOLD);
    return gold.getAmount() >= cost;
  }

  public Boolean hasEnoughFoodGeneration(Integer foodAmount, Kingdom kingdom) {
    Resource food = updateResourceOfKingdom(kingdom, ResourceType.FOOD);
    return food.getGeneration() >= foodAmount;
  }

  public Boolean hasEnoughFood(Integer foodAmount, Kingdom kingdom) {
    Resource food = updateResourceOfKingdom(kingdom, ResourceType.FOOD);
    return food.getAmount() >= foodAmount;
  }


  public void updateResourceGeneration(Kingdom kingdom, Building foundBuilding) {
    if (foundBuilding.getType().equals("Mine")) {
      Resource gold = getResourceFromList(kingdom, ResourceType.GOLD);
      gold.setGeneration(gold.getGeneration() + (foundBuilding.getLevel() * 5) + 5);
      resourceRepository.save(gold);
    }

    if (foundBuilding.getType().equals("Farm")) {
      Resource food = getResourceFromList(kingdom, ResourceType.FOOD);
      food.setGeneration(food.getGeneration() + (foundBuilding.getLevel() * 5) + 5);
      resourceRepository.save(food);
    }
  }

  public void updateResource(Resource gold) {
    resourceRepository.save(gold);
  }


  public void initializeResources(User user) {
    saveResource(renderResourceToKingdom(createResource(ResourceType.GOLD), user.getKingdom()));
    saveResource(renderResourceToKingdom(createResource(ResourceType.FOOD), user.getKingdom()));
  }

  public List<Resource> getResourcesByKingdom(Kingdom kingdom) {
    return resourceRepository.findResourcesByKingdom(kingdom);
  }

  public Integer lootGold(Kingdom ownKingdom, Kingdom enemyKingdom) {
    Resource enemyGold = getResourceFromList(enemyKingdom, ResourceType.GOLD);
    Integer lootGold = enemyGold.getAmount() / 2;
    enemyGold.setAmount(enemyGold.getAmount() - lootGold);
    saveResource(enemyGold);

    Resource ownGold = getResourceFromList(ownKingdom, ResourceType.GOLD);
    ownGold.setAmount(ownGold.getAmount() + lootGold);
    saveResource(ownGold);
    return lootGold;
  }

  public Integer lootFood(Kingdom ownKingdom, Kingdom enemyKingdom) {
    Resource enemyFood = getResourceFromList(enemyKingdom, ResourceType.FOOD);
    Integer lootFood = enemyFood.getAmount() / 2;
    enemyFood.setAmount(enemyFood.getAmount() - lootFood);
    saveResource(enemyFood);

    Resource ownFood = getResourceFromList(ownKingdom, ResourceType.FOOD);
    ownFood.setAmount(ownFood.getAmount() + lootFood);
    saveResource(ownFood);
    return lootFood;
  }

  public void checkForMissingResourceParameters(Kingdom kingdom) throws MissingResourcesException {
    List<String> missingParameterList = new ArrayList<>();
    checkIfNullOrLessThanTheCostField(getResourceFromList(kingdom, ResourceType.GOLD).getAmount(), "gold",
        missingParameterList);
    checkIfNullOrLessThanTheCostField(getResourceFromList(kingdom, ResourceType.FOOD).getAmount(), "food",
        missingParameterList);
    if (missingParameterList.size() > 0) {
      throw new MissingResourcesException(missingParameterList);
    }
  }

  private void checkIfNullOrLessThanTheCostField(Integer inputField, String fieldName,
                                                 List<String> missingParameterList) {
    if (inputField == null || inputField <= 100) {
      missingParameterList.add(fieldName);
    }
  }

}
