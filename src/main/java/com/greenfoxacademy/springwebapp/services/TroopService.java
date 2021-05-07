package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.exceptions.troops.InvalidTroopIdException;
import com.greenfoxacademy.springwebapp.exceptions.troops.NotYourTroopException;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.resources.Resource;
import com.greenfoxacademy.springwebapp.models.resources.ResourceType;
import com.greenfoxacademy.springwebapp.models.troops.Troop;
import com.greenfoxacademy.springwebapp.models.troops.TroopDTO;
import com.greenfoxacademy.springwebapp.models.users.User;
import com.greenfoxacademy.springwebapp.repositories.TroopRepository;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class TroopService {

  private TroopRepository troopRepository;
  private ResourceService resourceService;
  private ModelMapper modelMapper = new ModelMapper();

  @Autowired
  public TroopService(TroopRepository troopRepository,
                      ResourceService resourceService) {
    this.troopRepository = troopRepository;
    this.resourceService = resourceService;
  }

  public TroopService(TroopRepository troopRepository) {
    this.troopRepository = troopRepository;
  }

  public Troop createTroopForKingdom(Kingdom kingdom) {
    Troop troop = new Troop(kingdom);
    return troopRepository.save(troop);
  }


  public List<TroopDTO> troopToDTO(List<Troop> troopList) {

    return troopList.stream()
        .map(
            TroopDTO::new)
        .collect(Collectors.toList());
  }

  public Troop saveTroop(Troop troop) {
    return troopRepository.save(troop);
  }

  public List<Troop> getTroopListByKingdom(Kingdom kingdom) {
    getActualTroopStatus(kingdom);
    return troopRepository.findTroopsByKingdom(kingdom);
  }

  public void getActualTroopStatus(Kingdom kingdom) {
    if (!isEnoughFoodForTroop(kingdom)) {
      Integer deletedTroopNumber = resetTroop(kingdom);
      resetFood(kingdom, deletedTroopNumber);
    }
  }

  private void resetFood(Kingdom kingdom, Integer deletedTroopNumber) {
    Resource food = resourceService.getResourceFromList(kingdom, ResourceType.FOOD);
    food.setGeneration(food.getGeneration() + 5 * deletedTroopNumber);
    food.setAmount(0);
    resourceService.saveResource(food);
  }

  private Integer resetTroop(Kingdom kingdom) {
    List<Troop> troopList = troopRepository.findTroopsByKingdom(kingdom);
    Integer deletedTroopNumber = troopList.size();
    for (Troop troop : troopList) {
      troopRepository.delete(troop);
    }
    return deletedTroopNumber;
  }

  private boolean isEnoughFoodForTroop(Kingdom kingdom) {
    Resource food = resourceService.updateResourceOfKingdom(kingdom, ResourceType.FOOD);
    return food.getAmount() > 0;
  }


  public String getApproximateTroopNumber(Kingdom kingdom) {
    int bottomNumber = (kingdom.getTroops().size() / 5) * 5;
    int topNumber = bottomNumber + 4;
    return "between " + bottomNumber + " and " + topNumber;
  }

  public Integer sumAttackPoints(List<Troop> troopList) {
    Integer sum = 0;
    for (Troop troop : troopList) {
      sum = sum + troop.getAttack();
    }
    return sum;
  }

  public Integer sumDefensePoints(List<Troop> troopList) {
    Integer sum = 0;
    for (Troop troop : troopList) {
      sum = sum + troop.getDefense();
    }
    return sum;
  }

  public Integer sumHp(List<Troop> troopList) {
    Integer sum = 0;
    for (Troop troop : troopList) {
      sum = sum + troop.getHp();
    }
    return sum;
  }

  public Integer attackTroop(List<Troop> troopList1, List<Troop> troopList2) {
    Integer counter = 0;
    Integer remainingHp = sumHp(troopList1) + sumDefensePoints(troopList1)
        - sumAttackPoints(troopList2);
    for (int i = 0; i < troopList1.size(); i++) {
      if (remainingHp <= 0) {
        troopRepository.delete(troopList1.get(i));
        counter++;
      } else {
        remainingHp = remainingHp - troopList1.get(i).getHp();
      }
    }
    return counter;
  }

  public TroopDTO upgradeTroop(User user, Long troopId) throws InvalidTroopIdException, NotYourTroopException {
    Troop foundTroop = getTroopById(troopId);
    if (foundTroop.getKingdom().getId() == user.getKingdom().getId()) {
      foundTroop.setKingdom(user.getKingdom());
      foundTroop.setLevel(foundTroop.getLevel() + 1);
      saveTroop(foundTroop);
      return convertToDTO(foundTroop);
    } else {
      throw new NotYourTroopException(troopId);
    }
  }

  public Troop getTroopById(Long id) throws InvalidTroopIdException {
    if (troopRepository.findById(id).isPresent()) {
      return troopRepository.findById(id).get();
    } else {
      throw new InvalidTroopIdException(id);
    }
  }

  public TroopDTO convertToDTO(Troop troop) {
    return modelMapper.map(troop, TroopDTO.class);
  }
}