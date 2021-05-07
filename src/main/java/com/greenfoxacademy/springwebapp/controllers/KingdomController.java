package com.greenfoxacademy.springwebapp.controllers;

import com.greenfoxacademy.springwebapp.exceptions.kingdoms.KingdomException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.MissingResourcesException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NoSuchKingdomException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughResourceException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughTroopException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.ReservedKingdomnameException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.UnableToAttackYourselfException;
import com.greenfoxacademy.springwebapp.exceptions.troops.InvalidTroopIdException;
import com.greenfoxacademy.springwebapp.exceptions.troops.NotYourTroopException;
import com.greenfoxacademy.springwebapp.exceptions.users.MissingParameterException;
import com.greenfoxacademy.springwebapp.exceptions.users.NoSuchUserException;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingDTO;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingObjectDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.KingdomNameRequestDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.KingdomNameResponseDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.KingdomResponseDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.ScoutRequestDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.ScoutResponseDTO;
import com.greenfoxacademy.springwebapp.models.resources.ResourceDTO;
import com.greenfoxacademy.springwebapp.models.troops.TroopDTO;
import com.greenfoxacademy.springwebapp.models.troops.TroopResponseDTO;
import com.greenfoxacademy.springwebapp.services.BuildingService;
import com.greenfoxacademy.springwebapp.services.KingdomService;
import com.greenfoxacademy.springwebapp.services.TroopService;
import com.greenfoxacademy.springwebapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tribes")
public class KingdomController {

  private final UserService userService;
  private final KingdomService kingdomService;
  private final BuildingService buildingService;
  private final TroopService troopService;


  @Autowired
  public KingdomController(UserService userService,
                           KingdomService kingdomService, BuildingService buildingService,
                           TroopService troopService) {
    this.userService = userService;
    this.kingdomService = kingdomService;
    this.buildingService = buildingService;
    this.troopService = troopService;
  }


  @GetMapping("/kingdom")
  public ResponseEntity<List<KingdomResponseDTO>> getKingdoms() {
    return ResponseEntity.ok(kingdomService.getAllKingdomDTO());
  }

  @GetMapping("/kingdom/resource")
  public ResponseEntity<List<ResourceDTO>> listAllResources(
      @RequestHeader(value = "Amazonite-tribes-token", required = false) String token)
      throws NoSuchUserException, NoSuchKingdomException {
    return ResponseEntity.ok(kingdomService.listAllResources(userService.getUserByToken(token)));
  }

  @GetMapping("/kingdom/building")
  public ResponseEntity<List<BuildingObjectDTO>> listAllBuildings(
      @RequestHeader(value = "Amazonite-tribes-token", required = false) String token)
      throws NoSuchKingdomException, NoSuchUserException {
    return new ResponseEntity<>(kingdomService.listAllBuildings(userService.getUserByToken(token)),
        HttpStatus.OK);
  }

  @GetMapping("/kingdom/troop")
  public ResponseEntity<List<TroopDTO>> listAllTroops(
      @RequestHeader(value = "Amazonite-tribes-token", required = false) String token)
      throws NoSuchUserException, NoSuchKingdomException {
    return new ResponseEntity<>(kingdomService.listAllTroops(userService.getUserByToken(token)), HttpStatus.OK);
  }

  @PostMapping("/kingdom/troop")
  public ResponseEntity<TroopResponseDTO> addNewTroop(
      @RequestHeader(value = "Amazonite-tribes-token", required = false) String token)
      throws NotEnoughResourceException, NoSuchUserException {
    return ResponseEntity.ok(kingdomService.addNewTroopToKingdom(userService.getUserByToken(token)));
  }

  @PutMapping("/kingdom")
  public ResponseEntity<KingdomNameResponseDTO> changeKingdomName(
      @RequestHeader(value = "Amazonite-tribes-token", required = false) String token,
      @RequestBody(required = false) KingdomNameRequestDTO newKingdomName)
      throws ReservedKingdomnameException, MissingParameterException, NoSuchUserException {
    return ResponseEntity.ok(new KingdomNameResponseDTO(
        kingdomService.changeKingdomName(userService.getUserByToken(token), newKingdomName)));
  }

  @PutMapping("/kingdom/building/{id}")
  public ResponseEntity<BuildingDTO> updateBuildingDetails(
      @PathVariable Long id,
      @RequestHeader(value = "Amazonite-tribes-token", required = false) String token)
      throws NoSuchUserException, KingdomException, NotEnoughResourceException {

    return ResponseEntity.ok().body(buildingService
        .updateBuilding(userService.getUserByToken(token), userService.getUserByToken(token).getKingdom(), id));
  }

  @GetMapping("/kingdom/scout")
  public ResponseEntity<List<ScoutResponseDTO>> scout(
      @RequestHeader(value = "Amazonite-tribes-token", required = false) String token,
      @RequestBody(required = false) ScoutRequestDTO payment) throws NoSuchUserException, NotEnoughResourceException {

    return ResponseEntity.ok(kingdomService.scout(userService.getUserByToken(token), payment));
  }

  @GetMapping("/kingdom/attack/{id}")
  public ResponseEntity<?> goingToWar(@PathVariable Long id,
                                      @RequestHeader(value = "Amazonite-tribes-token") String token)
      throws NoSuchUserException, NoSuchKingdomException, UnableToAttackYourselfException,
      NotEnoughTroopException, MissingResourcesException {
    return ResponseEntity.ok(kingdomService.goingToWar(id, userService.getUserByToken(token)));
  }

  @PutMapping("/kingdom/troop/{id}")
  public ResponseEntity<TroopDTO> upgradeTroopLevel(@PathVariable Long id,
                                                       @RequestHeader("Amazonite-tribes-token") String token)
      throws NoSuchUserException, InvalidTroopIdException, NotYourTroopException {
    return ResponseEntity.ok().body(troopService.upgradeTroop(userService.getUserByToken(token), id));
  }
}
