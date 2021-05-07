package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.exceptions.kingdoms.MissingResourcesException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NoSuchKingdomException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughResourceException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughTroopException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.ReservedKingdomnameException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.UnableToAttackYourselfException;
import com.greenfoxacademy.springwebapp.exceptions.users.MissingParameterException;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingObjectDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.kingdoms.KingdomNameRequestDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.KingdomResponseDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.Location;
import com.greenfoxacademy.springwebapp.models.kingdoms.LocationDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.ScoutRequestDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.ScoutResponseDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.WarResultDTO;
import com.greenfoxacademy.springwebapp.models.resources.ResourceDTO;
import com.greenfoxacademy.springwebapp.models.resources.ResourceType;
import com.greenfoxacademy.springwebapp.models.troops.Troop;
import com.greenfoxacademy.springwebapp.models.troops.TroopDTO;
import com.greenfoxacademy.springwebapp.models.troops.TroopResponseDTO;
import com.greenfoxacademy.springwebapp.models.users.User;
import com.greenfoxacademy.springwebapp.repositories.KingdomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KingdomService {


  private final KingdomRepository kingdomRepository;
  private final LocationService locationService;
  private final ResourceService resourceService;
  private final BuildingService buildingService;
  private final TroopService troopService;
  private ModelMapper modelMapper;

  @Autowired
  public KingdomService(KingdomRepository kingdomRepository,
                        LocationService locationService,
                        ResourceService resourceService,
                        BuildingService buildingService, TroopService troopService) {
    this.kingdomRepository = kingdomRepository;
    this.locationService = locationService;
    this.buildingService = buildingService;
    this.troopService = troopService;
    this.resourceService = resourceService;
  }

  public Kingdom createKingdom(String kingdomName) {
    return new Kingdom(kingdomName, locationService.getNewLocation());
  }

  public Kingdom saveNewKingdom(String kingdomName) {
    Kingdom kingdom = createKingdom(kingdomName);
    addResourceToNewKingdom(kingdom);
    return kingdomRepository.save(kingdom);
  }

  public List<Kingdom> getAllKingdom() {
    return (List<Kingdom>) kingdomRepository.findAll();
  }

  public List<KingdomResponseDTO> getAllKingdomDTO() {
    return kingdomToDTO((List<Kingdom>) kingdomRepository.findAll());
  }

  public List<KingdomResponseDTO> kingdomToDTO(List<Kingdom> kingdomList) {

    return kingdomList.stream()
        .map(k -> new KingdomResponseDTO(k.getId(), k.getName(),
            troopService.troopToDTO(troopService.getTroopListByKingdom(k)),
            buildingService.buildingToDTO(k.getBuildings()),
            resourceService.resourceToDTO(k.getResources()),
            k.getUser().getUsername()))
        .collect(Collectors.toList());
  }

  public Kingdom saveKingdom(Kingdom kingdom) {
    return kingdomRepository.save(kingdom);
  }

  public List<BuildingObjectDTO> listAllBuildings(User user) throws NoSuchKingdomException {
    Kingdom kingdom = getKingdomById(user.getKingdom().getId());
    return buildingService.getBuildingsByKingdom_Id(kingdom.getId())
        .stream()
        .map(building -> new BuildingObjectDTO(building.getType(), building.getId(), building.getFinishedAt(),
            building.getLevel(), building.getStartedAt(), building.getKingdom().getId()))
        .collect(Collectors.toList());
  }

  public Kingdom getKingdomById(Long id) throws NoSuchKingdomException {
    return kingdomRepository.findById(id).orElseThrow(NoSuchKingdomException::new);
  }

  public List<TroopDTO> listAllTroops(User user) throws NoSuchKingdomException {
    Kingdom kingdom = getKingdomById(user.getKingdom().getId());
    return troopService.getTroopListByKingdom(kingdom)
        .stream()
        .map(troop -> new TroopDTO(troop.getId(), troop.getLevel(), troop.getHp(), troop.getAttack(),
            troop.getDefense(), troop.getStartedAt(), troop.getFinishedAt(), troop.getKingdom().getId()))
        .collect(Collectors.toList());
  }


  private void addResourceToNewKingdom(Kingdom kingdom) {
    kingdom.addResource(resourceService.saveResourceWithKingdomId(ResourceType.FOOD, kingdom));
    kingdom.addResource(resourceService.saveResourceWithKingdomId(ResourceType.GOLD, kingdom));
  }

  public List<ResourceDTO> listAllResources(User user) throws NoSuchKingdomException {
    Kingdom kingdom = getKingdomById(user.getKingdom().getId());
    resourceService.getActualResourcesOfKingdom(kingdom);
    return resourceService.getResourcesByKingdomId(kingdom.getId())
        .stream()
        .map(resource -> new ResourceDTO(resource.getType(), resource.getAmount(), resource.getGeneration(),
            resource.getKingdom().getId())).collect(
            Collectors.toList());

  }

  public List<String> getAllKingdomname() {
    return kingdomRepository.findAllKingdomname();
  }

  public boolean isKingdomnameOccupied(String kingdomname) {
    return getAllKingdomname().contains(kingdomname);
  }

  public TroopResponseDTO addNewTroopToKingdom(User user)
      throws NotEnoughResourceException {

    Kingdom kingdom = user.getKingdom();

    Integer cost = 25;
    Integer foodAmount = 5;

    Boolean isThereEnoughFood = checkFoodAndGold(kingdom, cost, foodAmount);
    resourceService.reduceResourceOfKingdom(cost, foodAmount, kingdom);

    Troop troop = saveNewTroopToKingdom(kingdom);
    return troopToResponseDTO(troop, isThereEnoughFood);
  }

  private Troop saveNewTroopToKingdom(Kingdom kingdom) {
    Troop troop = troopService.createTroopForKingdom(kingdom);
    kingdom.addTroop(troop);
    kingdomRepository.save(kingdom);
    return troop;
  }

  private TroopResponseDTO troopToResponseDTO(Troop troop, Boolean isThereEnoughFood) {
    modelMapper = new ModelMapper();
    TroopResponseDTO troopResponseDTO = modelMapper.map(troop, TroopResponseDTO.class);
    if (!isThereEnoughFood) {
      troopResponseDTO.setMessage("Warning! You have too many troops, your food generation value is negative. "
          + "When the food amount reaches 0, your troops will die!");
    }
    return troopResponseDTO;
  }

  private Boolean checkFoodAndGold(Kingdom kingdom, Integer cost, Integer foodAmount)
      throws NotEnoughResourceException {
    if (!resourceService.hasEnoughGold(cost, kingdom)) {
      throw new NotEnoughResourceException("Insufficient gold");
    }

    return resourceService.hasEnoughFoodGeneration(foodAmount, kingdom);
  }


  public String changeKingdomName(User user, KingdomNameRequestDTO kingdomName)
      throws ReservedKingdomnameException, MissingParameterException {
    isInputParameterValid(kingdomName);
    String name = kingdomName.getName();
    if (isKingdomnameOccupied(name)) {
      throw new ReservedKingdomnameException(name);
    }
    Kingdom kingdom = user.getKingdom();
    kingdom.setName(name);
    return kingdomRepository.save(kingdom).getName();
  }

  private void isInputParameterValid(KingdomNameRequestDTO kingdomName) throws MissingParameterException {
    if (kingdomName == null || kingdomName.getName() == null || kingdomName.getName().equals("")) {
      throw new MissingParameterException(Arrays.asList("name"));
    }
  }

  public List<ScoutResponseDTO> scout(User user, ScoutRequestDTO payment) throws NotEnoughResourceException {
    Integer gold = checkPayment(payment);
    Kingdom kingdom = user.getKingdom();
    payForScouting(gold, kingdom);
    List<ScoutResponseDTO> scoutedKingdoms = new ArrayList<>();
    for (Location location : getLocationsInRange(gold, kingdom)) {
      addScoutedDataToList(scoutedKingdoms, location, kingdomRepository.findByLocation(location), kingdom);
    }
    return scoutedKingdoms;
  }

  private Integer checkPayment(ScoutRequestDTO payment) throws NotEnoughResourceException {
    if (payment == null || payment.getGold() == null || payment.getGold() <= 0) {
      throw new NotEnoughResourceException("You have to pay for scouting!");
    }
    return payment.getGold();
  }

  private void payForScouting(Integer cost, Kingdom kingdom) throws NotEnoughResourceException {
    if (!resourceService.hasEnoughGold(cost, kingdom)) {
      throw new NotEnoughResourceException("Insufficient Gold for this range");
    }
    resourceService.reduceResourceOfKingdom(cost, 0, kingdom);
    kingdomRepository.save(kingdom);
  }

  private void addScoutedDataToList(List<ScoutResponseDTO> scoutedKingdoms, Location location,
                                    Optional<Kingdom> optionalKingdom, Kingdom ownKingdom) {
    if (optionalKingdom.isPresent()) {
      Kingdom kingdom = optionalKingdom.get();
      if (!kingdom.getId().equals(ownKingdom.getId())) {
        ScoutResponseDTO scoutedKingdom =
            new ScoutResponseDTO(new LocationDTO(location.getX(), location.getY()),
                kingdom.getId(), kingdom.getName());
        scoutedKingdom.setTroop_number(troopService.getApproximateTroopNumber(optionalKingdom.get()));
        scoutedKingdoms.add(scoutedKingdom);
      }
    }
  }

  private List<Location> getLocationsInRange(Integer range, Kingdom kingdom) {
    List<Location> scoutedLocations = locationService.getAllLocations().stream()
        .filter(location ->
            (location.getX() > kingdom.getLocation().getX() - (range * 100))
                && (location.getX() < kingdom.getLocation().getX() + (range * 100))
                && (location.getY() > kingdom.getLocation().getY() - (range * 100))
                && (location.getY() < kingdom.getLocation().getY() + (range * 100)))
        .collect(Collectors.toList());
    return scoutedLocations;
  }

  public WarResultDTO goingToWar(Long kingdomId, User user)
      throws NoSuchKingdomException, UnableToAttackYourselfException, MissingResourcesException,
      NotEnoughTroopException {
    List<Troop> ownTroopList = troopService.getTroopListByKingdom(user.getKingdom());
    if (ownTroopList.size() == 0) {
      throw new NotEnoughTroopException();
    }
    if (kingdomId.equals(user.getKingdom().getId())) {
      throw new UnableToAttackYourselfException();
    }
    if (resourceService.hasEnoughGold(100, user.getKingdom())
        && resourceService.hasEnoughFood(100, user.getKingdom())
    ) {
      resourceService.reduceGold(100, user.getKingdom().getResources());
      resourceService.reduceFood(100, user.getKingdom().getResources());
      Kingdom enemyKingdom = getKingdomById(kingdomId);
      List<Troop> enemyTroopList = troopService.getTroopListByKingdom(enemyKingdom);
      Integer troopsKilled = troopService.attackTroop(enemyTroopList, ownTroopList);
      Integer troopsLost = troopService.attackTroop(ownTroopList, enemyTroopList);
      if (troopService.getTroopListByKingdom(enemyKingdom).size() == 0) {
        Integer lootGold = resourceService.lootGold(user.getKingdom(), enemyKingdom);
        Integer lootFood = resourceService.lootFood(user.getKingdom(), enemyKingdom);
        return new WarResultDTO("win", lootGold, lootFood, troopsLost, troopsKilled);
      } else {
        return new WarResultDTO("lost", troopsLost, troopsKilled);
      }
    } else {
      resourceService.checkForMissingResourceParameters(user.getKingdom());
      throw new MissingResourcesException(Arrays.asList("gold", "food"));
    }
  }


}