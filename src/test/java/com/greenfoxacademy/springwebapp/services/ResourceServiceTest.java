package com.greenfoxacademy.springwebapp.services;


import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.kingdoms.Location;
import com.greenfoxacademy.springwebapp.models.resources.Food;
import com.greenfoxacademy.springwebapp.models.resources.Gold;
import com.greenfoxacademy.springwebapp.models.resources.Resource;
import com.greenfoxacademy.springwebapp.models.resources.ResourceDTO;
import com.greenfoxacademy.springwebapp.models.resources.ResourceFactory;
import com.greenfoxacademy.springwebapp.models.resources.ResourceType;
import com.greenfoxacademy.springwebapp.repositories.ResourceRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ResourceServiceTest {

  ResourceService resourceService;
  ResourceRepository mockResourceRepository;
  ResourceFactory mockResourceFactory;
  TimeService mockTimeService;

  private final Resource mockResource = new Gold(10, 5);
  private final List<Resource> resourceList = new ArrayList<>();


  @Before
  public void setUp() {
    mockResourceRepository = Mockito.mock(ResourceRepository.class);
    mockResourceFactory = Mockito.mock(ResourceFactory.class);
    mockTimeService = Mockito.mock(TimeService.class);
    resourceService = new ResourceService(mockResourceRepository, mockResourceFactory, mockTimeService);
  }

  @Test
  public void updateAllResourcesOfKingdom_happyCase() {
    Kingdom mockKingdom = new Kingdom("DummyKingdom", new Location(1, 1));
    mockKingdom.setId(1L);
    Resource mockGold = new Gold(mockKingdom, 2L, 10, 10, new Timestamp(0L));
    Resource mockFood = new Food(mockKingdom, 3L, 10, 10, new Timestamp(0L));
    mockKingdom.setResources(Arrays.asList(mockGold, mockFood));

    when(mockTimeService.minutesBetweenATimestampAndTheActualTime(mockGold.getLastUpdated()))
        .thenReturn(1L);
    when(mockTimeService.minutesBetweenATimestampAndTheActualTime(mockFood.getLastUpdated()))
        .thenReturn(1L);
    when(mockResourceRepository.findResourcesByKingdom_id(mockKingdom.getId())).thenReturn(mockKingdom.getResources());

    Resource resultGold = new Gold(mockKingdom, 2L, 25, 10, new Timestamp(60000));
    Resource resultFood = new Food(mockKingdom, 3L, 25, 10, new Timestamp(60000));

    assertEquals(resultGold.getAmount(),
        resourceService.getActualResourcesOfKingdom(mockKingdom).get(0).getAmount());
    assertEquals(resultFood.getLastUpdated(),
        resourceService.getActualResourcesOfKingdom(mockKingdom).get(1).getLastUpdated());
  }

  @Test
  public void updateResourceOfKingdom_happyCase() {
    Kingdom mockKingdom = new Kingdom("DummyKingdom", new Location(1, 1));
    mockKingdom.setId(1L);
    Resource mockGold = new Gold(mockKingdom, 2L, 10, 10, new Timestamp(0L));
    mockKingdom.setResources(Arrays.asList(mockGold));

    when(mockTimeService.minutesBetweenATimestampAndTheActualTime(mockGold.getLastUpdated()))
        .thenReturn(1L);
    when(mockResourceRepository.save(any())).thenReturn(mockGold);
    when(mockResourceRepository.findResourcesByKingdom_id(mockKingdom.getId())).thenReturn(mockKingdom.getResources());

    Resource resultGold = new Gold(mockKingdom, 2L, 25, 10, new Timestamp(60000));

    assertEquals(resultGold.getLastUpdated(),
        resourceService.updateResourceOfKingdom(mockKingdom, ResourceType.GOLD).getLastUpdated());
    assertEquals(resultGold.getAmount(),
        resourceService.updateResourceOfKingdom(mockKingdom, ResourceType.GOLD).getAmount());
  }

  @Test
  public void updateResourceOfKingdom_noResourceFound_creatingNewOne() {
    Kingdom mockKingdom = new Kingdom("DummyKingdom", new Location(1, 1));
    mockKingdom.setId(1L);

    Resource mockFood = new Food(mockKingdom, 2L, 0, 0, new Timestamp(0L));

    when(mockResourceFactory.createResource(ResourceType.FOOD)).thenReturn(new Food(0, 0));
    when(mockTimeService.minutesBetweenATimestampAndTheActualTime(mockFood.getLastUpdated()))
        .thenReturn(0L);
    when(mockResourceRepository.save(any())).thenReturn(mockFood);

    Resource resultFood = new Food(mockKingdom, 2L, 0, 0, new Timestamp(mockFood.getLastUpdated().getTime()));

    assertEquals(resultFood.getLastUpdated(),
        resourceService.updateResourceOfKingdom(mockKingdom, ResourceType.FOOD).getLastUpdated());
    assertEquals(resultFood.getAmount(),
        resourceService.updateResourceOfKingdom(mockKingdom, ResourceType.FOOD).getAmount());
  }

  @Test
  public void givenResourceList_whenResourceListHasOneResource_thenReturnResourceDTOListWithOneElement() {
    resourceList.add(mockResource);
    List<ResourceDTO> actual = resourceService.resourceToDTO(resourceList);
    assertEquals(1, actual.size());
  }

  @Test
  public void hasEnoughGold_trueCase() {
    Kingdom mockKingdom = new Kingdom("DummyKingdom", new Location(1, 1));
    mockKingdom.setId(1L);
    Integer amount = 10;
    Resource mockGold = new Gold(mockKingdom, 2L, amount, 10, new Timestamp(0L));
    mockKingdom.setResources(Arrays.asList(mockGold));

    when(mockTimeService.minutesBetweenATimestampAndTheActualTime(mockGold.getLastUpdated())).thenReturn(1L);
    when(mockResourceRepository.save(any())).thenReturn(mockGold);
    when(mockResourceRepository.findResourcesByKingdom_id(mockKingdom.getId())).thenReturn(mockKingdom.getResources());

    assertTrue(resourceService.hasEnoughGold(amount, mockKingdom));
  }

  @Test
  public void hasEnoughGold_falseCase() {
    Kingdom mockKingdom = new Kingdom("DummyKingdom", new Location(1, 1));
    mockKingdom.setId(1L);
    Integer amount = 10;
    Integer generation = 10;
    Resource mockGold = new Gold(mockKingdom, 2L, amount, generation, new Timestamp(0L));
    mockKingdom.setResources(Arrays.asList(mockGold));

    when(mockTimeService.minutesBetweenATimestampAndTheActualTime(mockGold.getLastUpdated())).thenReturn(1L);
    when(mockResourceRepository.save(any())).thenReturn(mockGold);
    when(mockResourceRepository.findResourcesByKingdom_id(mockKingdom.getId())).thenReturn(mockKingdom.getResources());

    assertFalse(resourceService.hasEnoughGold(amount + generation + 6, mockKingdom));
  }

  @Test
  public void hasEnoughFood_trueCase() {
    Kingdom mockKingdom = new Kingdom("DummyKingdom", new Location(1, 1));
    mockKingdom.setId(1L);
    Integer generation = 10;
    Resource mockFood = new Food(mockKingdom, 2L, 10, generation, new Timestamp(0L));
    mockKingdom.setResources(Arrays.asList(mockFood));

    when(mockTimeService.minutesBetweenATimestampAndTheActualTime(mockFood.getLastUpdated())).thenReturn(1L);
    when(mockResourceRepository.save(any())).thenReturn(mockFood);
    when(mockResourceRepository.findResourcesByKingdom_id(mockKingdom.getId())).thenReturn(mockKingdom.getResources());

    assertTrue(resourceService.hasEnoughFood(generation, mockKingdom));
  }

  @Test
  public void hasEnoughFoodGeneration_falseCase() {
    Kingdom mockKingdom = new Kingdom("DummyKingdom", new Location(1, 1));
    mockKingdom.setId(1L);
    Integer generation = 10;
    Resource mockFood = new Food(mockKingdom, 2L, 10, generation, new Timestamp(0L));
    mockKingdom.setResources(Arrays.asList(mockFood));

    when(mockTimeService.minutesBetweenATimestampAndTheActualTime(mockFood.getLastUpdated())).thenReturn(1L);
    when(mockResourceRepository.save(any())).thenReturn(mockFood);
    when(mockResourceRepository.findResourcesByKingdom_id(mockKingdom.getId())).thenReturn(mockKingdom.getResources());

    assertFalse(resourceService.hasEnoughFoodGeneration(generation + 1, mockKingdom));
  }

}

