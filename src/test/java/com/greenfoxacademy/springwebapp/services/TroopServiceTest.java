package com.greenfoxacademy.springwebapp.services;


import com.greenfoxacademy.springwebapp.exceptions.troops.InvalidTroopIdException;
import com.greenfoxacademy.springwebapp.exceptions.troops.NotYourTroopException;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.troops.Troop;
import com.greenfoxacademy.springwebapp.models.troops.TroopDTO;
import com.greenfoxacademy.springwebapp.models.users.User;
import com.greenfoxacademy.springwebapp.repositories.TroopRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class TroopServiceTest {

  TroopService troopService;
  TroopRepository mockTroopRepository;
  private final Kingdom mockKingdom = new Kingdom();
  private final User mockUser = new User();
  private final Troop mockTroop = new Troop();
  private final List<Troop> troopList = new ArrayList<>();

  @Before
  public void setUp() {
    mockTroopRepository = Mockito.mock(TroopRepository.class);
    troopService = new TroopService(mockTroopRepository);
  }

  @Test
  public void givenTroopList_whenTroopListHasOneResource_thenReturnTroopDTOListWithOneElement() {
    troopList.add(mockTroop);
    List<TroopDTO> actual = troopService.troopToDTO(troopList);
    assertEquals(1, actual.size());
  }

  @Test
  public void upgradeTroop() throws InvalidTroopIdException, NotYourTroopException {
    mockUser.setKingdom(mockKingdom);
    mockTroop.setKingdom(mockKingdom);
    mockTroop.setId(1L);

    when(mockTroopRepository.findById(any())).thenReturn(java.util.Optional.of(mockTroop));
    TroopDTO troopDTO = new TroopDTO(mockTroop);
    troopDTO.setLevel(troopDTO.getLevel() + 1);

    assertEquals(troopDTO.getLevel(), troopService.upgradeTroop(mockUser, mockTroop.getId()).getLevel());
  }

  @Test
  public void getTroopById() throws InvalidTroopIdException {
    when(mockTroopRepository.findById(any())).thenReturn(java.util.Optional.of(mockTroop));
    assertEquals(mockTroop, troopService.getTroopById(1L));
  }

  @Test
  public void convertToDTO() {
    TroopDTO troopDTO = troopService.convertToDTO(mockTroop);
    assertEquals(mockTroop.getFinishedAt(), troopDTO.getFinishedAt());
  }
}