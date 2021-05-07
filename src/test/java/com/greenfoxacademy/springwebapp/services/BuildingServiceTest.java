package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.models.buildings.Building;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingDTO;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingFactory;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingType;
import com.greenfoxacademy.springwebapp.models.buildings.TownHall;
import com.greenfoxacademy.springwebapp.repositories.BuildingRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class BuildingServiceTest {


  private BuildingService buildingService;
  private BuildingRepository mockBuildingRepository;
  private BuildingFactory mockBuildingFactory;
  private ResourceService mockResourceService;
  private final Building mockBuilding = new TownHall();
  private final List<Building> buildingList = new ArrayList<>();

  @Before
  public void setUp() {
    mockBuildingRepository = Mockito.mock(BuildingRepository.class);
    mockBuildingFactory = Mockito.mock(BuildingFactory.class);
    mockResourceService = Mockito.mock(ResourceService.class);
    buildingService =
        new BuildingService(mockBuildingRepository, mockBuildingFactory, mockResourceService);
  }


  @Test
  public void createBuilding() {
    BuildingType buildingType = BuildingType.TOWNHALL;
    when(mockBuildingFactory.createBuilding(buildingType)).thenReturn(mockBuilding);

    Building newBuilding = buildingService.createBuilding(buildingType);
    assertEquals(newBuilding, mockBuilding);
  }


  @Test
  public void givenBuildingList_whenBuildingListHasOneBuilding_thenReturnBuildingDTOListWithOneElement() {
    buildingList.add(mockBuilding);
    List<BuildingDTO> actual = buildingService.buildingToDTO(buildingList);
    assertEquals(1, actual.size());
  }
}