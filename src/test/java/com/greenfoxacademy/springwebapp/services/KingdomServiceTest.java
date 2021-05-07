package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.exceptions.kingdoms.MissingResourcesException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NoSuchKingdomException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughResourceException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughTroopException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.ReservedKingdomnameException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.UnableToAttackYourselfException;
import com.greenfoxacademy.springwebapp.exceptions.users.MissingParameterException;
import com.greenfoxacademy.springwebapp.models.buildings.Building;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingObjectDTO;
import com.greenfoxacademy.springwebapp.models.buildings.Farm;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.kingdoms.KingdomNameRequestDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.KingdomResponseDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.Location;
import com.greenfoxacademy.springwebapp.models.kingdoms.LocationDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.ScoutRequestDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.ScoutResponseDTO;
import com.greenfoxacademy.springwebapp.models.resources.Food;
import com.greenfoxacademy.springwebapp.models.resources.Gold;
import com.greenfoxacademy.springwebapp.models.resources.Resource;
import com.greenfoxacademy.springwebapp.models.resources.ResourceDTO;
import com.greenfoxacademy.springwebapp.models.resources.ResourceType;
import com.greenfoxacademy.springwebapp.models.troops.Troop;
import com.greenfoxacademy.springwebapp.models.troops.TroopResponseDTO;
import com.greenfoxacademy.springwebapp.models.users.User;
import com.greenfoxacademy.springwebapp.repositories.BuildingRepository;
import com.greenfoxacademy.springwebapp.repositories.KingdomRepository;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import com.greenfoxacademy.springwebapp.security.JwtUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class KingdomServiceTest {

  KingdomService kingdomService;
  LocationService mockLocationService;
  ResourceService mockResourceService;
  KingdomRepository mockKingdomRepository;
  BuildingService mockBuildingService;
  TroopService mockTroopService;
  JwtUtil mockJwtUtil;
  UserRepository mockUserRepository;
  BuildingRepository mockBuildingRepository;
  ModelMapper modelMapper;

  private final List<Kingdom> kingdomList = new ArrayList<>();

  @Before
  public void setUp() {
    mockKingdomRepository = Mockito.mock(KingdomRepository.class);
    mockLocationService = Mockito.mock(LocationService.class);
    mockTroopService = Mockito.mock(TroopService.class);
    mockBuildingService = Mockito.mock(BuildingService.class);
    mockResourceService = Mockito.mock(ResourceService.class);
    mockJwtUtil = Mockito.mock(JwtUtil.class);
    mockUserRepository = Mockito.mock(UserRepository.class);
    mockBuildingRepository = Mockito.mock(BuildingRepository.class);
    kingdomService =
        new KingdomService(mockKingdomRepository,
            mockLocationService, mockResourceService, mockBuildingService, mockTroopService);
  }


  @Test
  public void createKingdom() {
    Location mockLocation = new Location(15, 15);
    String username = "DummyUser";
    Kingdom mockKingdom = new Kingdom(username, mockLocation);

    when(mockLocationService.getNewLocation()).thenReturn(new Location(15, 15));

    assertEquals(mockKingdom.getName(), kingdomService.createKingdom(username).getName());
    assertEquals(mockKingdom.getLocation(), kingdomService.createKingdom(username).getLocation());
  }

  @Test
  public void givenKingdomList_whenKingdomRepositoryIsEmpty_thenReturnEmptyKingdomDTOList() {
    List<KingdomResponseDTO> actual = kingdomService.kingdomToDTO(kingdomList);
    assertEquals(0, actual.size());
  }

  @Test
  public void getKingdomById() throws NoSuchKingdomException {
    Long mockId = 1L;
    String mockUsername = "DummyUser1";
    Location mockLocation = new Location(15, 15);
    Kingdom mockKingdom = new Kingdom(mockUsername, mockLocation);
    when(mockKingdomRepository.findById(mockId)).thenReturn(Optional.of(mockKingdom));

    assertEquals(mockKingdom.getName(), kingdomService.getKingdomById(mockId).getName());
  }

  @Test
  public void listAllBuildings() throws NoSuchKingdomException {
    String mockUsername = "DummyUser1";
    String mockPassword = "asdasdasd";
    String mockKingdomName = "KingdomNevecske";
    Long mockId = 1L;

    Location mockLocation = new Location(15, 15);
    Kingdom mockKingdom = new Kingdom(mockKingdomName, mockLocation);
    mockKingdom.setId(mockId);
    when(mockKingdomRepository.findById(mockId)).thenReturn(Optional.of(mockKingdom));

    User mockUser = new User(mockUsername, mockKingdom, mockPassword);
    mockUser.setKingdom(mockKingdom);

    String mockToken = "asd.asd.asd";

    when(mockJwtUtil.extractUsername(mockToken)).thenReturn(mockUsername);

    Building mockFarm = new Farm();
    mockFarm.setKingdom(mockKingdom);
    List<Building> mockBuildingList = new ArrayList<>();
    mockBuildingList.add(mockFarm);

    when(mockBuildingService.getBuildingsByKingdom_Id(mockKingdom.getId())).thenReturn(mockBuildingList);

    BuildingObjectDTO mockObject = new BuildingObjectDTO();
    mockObject.setKingdomId(mockFarm.getKingdom().getId());
    mockObject.setDtype(mockFarm.getType());
    List<BuildingObjectDTO> mockList = new ArrayList<>();
    mockList.add(mockObject);

    assertEquals(mockList.stream().findFirst().get().getDtype(),
        kingdomService.listAllBuildings(mockUser).get(0).getDtype());
  }

  @Test
  public void listAllResources() throws NoSuchKingdomException {

    String mockUsername = "DummyUser1";
    String mockPassword = "asdasdasd";
    String mockKingdomName = "KingdomNevecske";
    Long mockId = 1L;

    Location mockLocation = new Location(15, 15);
    Kingdom mockKingdom = new Kingdom(mockKingdomName, mockLocation);
    mockKingdom.setId(mockId);
    when(mockKingdomRepository.findById(mockId)).thenReturn(Optional.of(mockKingdom));

    User mockUser = new User(mockUsername, mockKingdom, mockPassword);
    mockUser.setKingdom(mockKingdom);

    String mockToken = "asd.asd.asd";

    when(mockJwtUtil.extractUsername(mockToken)).thenReturn(mockUsername);

    Resource mockGold = new Gold();
    mockGold.setKingdom(mockKingdom);
    List<Resource> mockResourceList = new ArrayList<>();
    mockResourceList.add(mockGold);

    when(mockResourceService.getResourcesByKingdomId(mockKingdom.getId())).thenReturn(mockResourceList);

    ResourceDTO mockObject = new ResourceDTO();
    mockObject.setKingdomId(mockGold.getKingdom().getId());
    mockObject.setType(mockGold.getType());
    List<ResourceDTO> mockList = new ArrayList<>();
    mockList.add(mockObject);

    assertEquals(mockList.stream().findFirst().get().getType(),
        kingdomService.listAllResources(mockUser).get(0).getType());
  }


  @Test
  public void addNewTroopToKingdom_happyCase() throws NotEnoughResourceException {
    Kingdom mockKingdom = new Kingdom(1L, "TestKingdom", new Location(1, 1));
    Troop mockTroop = new Troop(mockKingdom);
    mockTroop.setId(5L);

    when(mockResourceService.hasEnoughGold(25, mockKingdom)).thenReturn(true);
    when(mockResourceService.hasEnoughFoodGeneration(5, mockKingdom)).thenReturn(true);
    when(mockTroopService.createTroopForKingdom(mockKingdom)).thenReturn(mockTroop);
    when(mockKingdomRepository.save(any())).thenReturn(mockKingdom);

    modelMapper = new ModelMapper();
    TroopResponseDTO mockTroopResponseDTO = modelMapper.map(new Troop(mockKingdom), TroopResponseDTO.class);
    mockTroopResponseDTO.setId(5L);
    User mockUser = new User("TestUser", mockKingdom);

    assertEquals(mockTroopResponseDTO, kingdomService.addNewTroopToKingdom(mockUser));
  }

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void addNewTroopToKingdom_notEnoughGold() throws NotEnoughResourceException {
    Kingdom mockKingdom = new Kingdom(1L, "TestKingdom", new Location(1, 1));
    Troop mockTroop = new Troop(mockKingdom);
    mockTroop.setId(5L);

    when(mockResourceService.hasEnoughGold(25, mockKingdom)).thenReturn(false);
    User mockUser = new User("TestUser", mockKingdom);

    exceptionRule.expect(NotEnoughResourceException.class);
    exceptionRule.expectMessage("Insufficient gold");
    kingdomService.addNewTroopToKingdom(mockUser);
  }

  @Test
  public void addNewTroopToKingdom_whenNotEnoughFood_GetErrorInResponseDTO()
      throws NotEnoughResourceException {
    Kingdom mockKingdom = new Kingdom(1L, "TestKingdom", new Location(1, 1));
    Troop mockTroop = new Troop(mockKingdom);
    mockTroop.setId(5L);

    when(mockResourceService.hasEnoughGold(25, mockKingdom)).thenReturn(true);
    when(mockResourceService.hasEnoughFood(5, mockKingdom)).thenReturn(false);
    when(mockTroopService.createTroopForKingdom(mockKingdom)).thenReturn(mockTroop);
    when(mockKingdomRepository.save(any())).thenReturn(mockKingdom);

    String message = "Warning! You have too many troops, your food generation value is negative. "
        + "When the food amount reaches 0, your troops will die!";
    User mockUser = new User("TestUser", mockKingdom);

    assertEquals(message, kingdomService.addNewTroopToKingdom(mockUser).getMessage());
  }

  @Test
  public void changeKingdomName_happyCase() throws ReservedKingdomnameException, MissingParameterException {
    Location location = new Location(13, 13);
    Kingdom mockKingdom = new Kingdom("TestKingdom", location);
    User mockUser = new User("TestUser", mockKingdom, "password");
    String newName = "NewKingdomName";
    KingdomNameRequestDTO mockKingdomNameRequestDTO = new KingdomNameRequestDTO(newName);
    Kingdom newKingdom = new Kingdom(newName, location);

    when(mockKingdomRepository.save(any())).thenReturn(newKingdom);
    when(mockKingdomRepository.findAllKingdomname()).thenReturn(Arrays.asList("KingdomName1", "KingdomName2"));

    assertEquals(newName, kingdomService.changeKingdomName(mockUser, mockKingdomNameRequestDTO));
  }

  @Test(expected = ReservedKingdomnameException.class)
  public void changeKingdomName_whenKingdomNameIsOccupied_thenReturnException()
      throws ReservedKingdomnameException, MissingParameterException {
    Location location = new Location(13, 13);
    Kingdom mockKingdom = new Kingdom("TestKingdom", location);
    User mockUser = new User("TestUser", mockKingdom, "password");
    String newName = "NewKingdomName";
    KingdomNameRequestDTO mockKingdomNameRequestDTO = new KingdomNameRequestDTO(newName);

    when(mockKingdomRepository.findAllKingdomname()).thenReturn(Arrays.asList("KingdomName1", "NewKingdomName"));

    kingdomService.changeKingdomName(mockUser, mockKingdomNameRequestDTO);
  }

  @Test(expected = MissingParameterException.class)
  public void changeKingdomName_whenKingdomNameRequestDTOIsMissing_thenReturnException()
      throws ReservedKingdomnameException, MissingParameterException {
    Location location = new Location(13, 13);
    Kingdom mockKingdom = new Kingdom("TestKingdom", location);
    User mockUser = new User("TestUser", mockKingdom, "password");

    kingdomService.changeKingdomName(mockUser, null);
  }

  @Test(expected = MissingParameterException.class)
  public void changeKingdomName_whenKingdomNameRequestDTOIsEmpty_thenReturnException()
      throws ReservedKingdomnameException, MissingParameterException {
    Location location = new Location(13, 13);
    Kingdom mockKingdom = new Kingdom("TestKingdom", location);
    User mockUser = new User("TestUser", mockKingdom, "password");
    KingdomNameRequestDTO mockKingdomNameRequestDTO = new KingdomNameRequestDTO(null);

    kingdomService.changeKingdomName(mockUser, mockKingdomNameRequestDTO);
  }

  @Test(expected = MissingParameterException.class)
  public void changeKingdomName_whenKingdomNameRequestDTOIsEmptyString_thenReturnException()
      throws ReservedKingdomnameException, MissingParameterException {
    Location location = new Location(13, 13);
    Kingdom mockKingdom = new Kingdom("TestKingdom", location);
    User mockUser = new User("TestUser", mockKingdom, "password");
    KingdomNameRequestDTO mockKingdomNameRequestDTO = new KingdomNameRequestDTO("");

    kingdomService.changeKingdomName(mockUser, mockKingdomNameRequestDTO);
  }


  @Test
  public void scout_happyCase() throws NotEnoughResourceException {
    Kingdom mockKingdom = new Kingdom(1L, "TestKingdom", new Location(150, 150));
    Location location2 = new Location(100, 100);
    Kingdom kingdom2 = new Kingdom(2L, "Kingdom2", location2);
    Location location3 = new Location(100, 800);
    Kingdom kingdom3 = new Kingdom(3L, "Kingdom3", location3);
    Location location4 = new Location(651, 651);
    Kingdom kingdom4 = new Kingdom(4L, "Kingdom4", location4);
    Location location5 = new Location(600, 150);
    Kingdom kingdom5 = new Kingdom(5L, "Kingdom5", location5);
    List<Location> locationList =
        Arrays.asList(kingdom2.getLocation(), kingdom3.getLocation(), kingdom4.getLocation(), kingdom5.getLocation());
    Integer cost = 5;

    when(mockResourceService.hasEnoughGold(cost, mockKingdom)).thenReturn(true);
    when(mockLocationService.getAllLocations()).thenReturn(locationList);
    when(mockKingdomRepository.findByLocation(location2)).thenReturn(Optional.of(kingdom2));
    when(mockKingdomRepository.findByLocation(location3)).thenReturn(Optional.of(kingdom3));
    when(mockKingdomRepository.findByLocation(location4)).thenReturn(Optional.of(kingdom4));
    when(mockKingdomRepository.findByLocation(location5)).thenReturn(Optional.of(kingdom5));
    when(mockTroopService.getApproximateTroopNumber(kingdom2)).thenReturn("between 0 and 4");
    when(mockTroopService.getApproximateTroopNumber(kingdom5)).thenReturn("between 5 and 9");

    User mockUser = new User("TestUser", mockKingdom);
    ScoutRequestDTO payment = new ScoutRequestDTO(cost);
    List<ScoutResponseDTO> actualScoutResponseDTOList = kingdomService.scout(mockUser, payment);
    List<ScoutResponseDTO> expectedScoutResponseDTOList =
        Arrays.asList(new ScoutResponseDTO(new LocationDTO(100, 100), 2L, "Kingdom2", "between 0 and 4"),
            new ScoutResponseDTO(new LocationDTO(600, 150), 5L, "Kingdom5", "between 5 and 9"));

    assertEquals(2, actualScoutResponseDTOList.size());
    assertEquals(expectedScoutResponseDTOList.get(0), actualScoutResponseDTOList.get(0));
    assertEquals(expectedScoutResponseDTOList.get(1), actualScoutResponseDTOList.get(1));
  }

  @Test
  public void scout_nullInputAsPayment_expectException() throws NotEnoughResourceException {
    Kingdom mockKingdom = new Kingdom(1L, "TestKingdom", new Location(150, 150));
    User mockUser = new User("TestUser", mockKingdom);

    exceptionRule.expect(NotEnoughResourceException.class);
    exceptionRule.expectMessage("You have to pay for scouting!");
    kingdomService.scout(mockUser, null);
  }

  @Test
  public void scout_notEnoughGold_expectException() throws NotEnoughResourceException {
    Kingdom mockKingdom = new Kingdom(1L, "TestKingdom", new Location(150, 150));
    Integer cost = 5;

    when(mockResourceService.hasEnoughGold(cost, mockKingdom)).thenReturn(false);
    User mockUser = new User("TestUser", mockKingdom);

    exceptionRule.expect(NotEnoughResourceException.class);
    exceptionRule.expectMessage("Insufficient Gold for this range");
    kingdomService.scout(mockUser, new ScoutRequestDTO(cost));
  }

  @Test(expected = NotEnoughTroopException.class)
  public void goingToWar_whenWithZeroTroops_thenReturnException()
      throws UnableToAttackYourselfException, MissingResourcesException, NotEnoughTroopException,
      NoSuchKingdomException {
    Location location = new Location(13, 13);
    Kingdom mockKingdom = new Kingdom("TestKingdom", location);
    User mockUser = new User("TestUser", mockKingdom, "password");

    kingdomService.goingToWar(mockKingdom.getId(), mockUser);
  }

  @Test(expected = UnableToAttackYourselfException.class)
  public void goingToWar_whenAttackYourself_thenReturnException()
      throws UnableToAttackYourselfException, MissingResourcesException, NotEnoughTroopException,
      NoSuchKingdomException {
    Kingdom mockKingdom = new Kingdom(1L, "TestKingdom", new Location(1, 1));
    Resource mockGold = new Gold(200, 10);
    mockGold.setKingdom(mockKingdom);
    Resource mockFood = new Food(200, 10);
    mockFood.setKingdom(mockKingdom);
    mockKingdom.setResources(Arrays.asList(mockGold, mockFood));
    Troop mockTroop = new Troop(mockKingdom);
    List<Troop> mockTroopList = new ArrayList<>(Arrays.asList(mockTroop));

    when(mockResourceService.updateResourceOfKingdom(mockKingdom, ResourceType.GOLD)).thenReturn(mockGold);
    when(mockResourceService.updateResourceOfKingdom(mockKingdom, ResourceType.FOOD)).thenReturn(mockFood);
    when(mockTroopService.getTroopListByKingdom(mockKingdom)).thenReturn(mockTroopList);
    when(mockKingdomRepository.save(any())).thenReturn(mockKingdom);

    User mockUser = new User("TestUser", mockKingdom, "password");

    kingdomService.goingToWar(mockUser.getKingdom().getId(), mockUser);
  }

  @Test(expected = MissingResourcesException.class)
  public void goingToWar_whenMissingResources_thenReturnException()
      throws UnableToAttackYourselfException, MissingResourcesException, NotEnoughTroopException,
      NoSuchKingdomException {
    Kingdom mockKingdom = new Kingdom(1L, "TestKingdom", new Location(1, 1));
    Kingdom mockEnemyKingdom = new Kingdom(2L, "TestKingdom2", new Location(2, 2));
    User mockUser = new User("TestUser", mockKingdom, "password");
    Troop mockTroop = new Troop(mockKingdom);
    List<Troop> mockTroopList = new ArrayList<>(Arrays.asList(mockTroop));

    when(mockTroopService.getTroopListByKingdom(mockKingdom)).thenReturn(mockTroopList);
    when(mockKingdomRepository.save(any())).thenReturn(mockKingdom);

    kingdomService.goingToWar(mockEnemyKingdom.getId(), mockUser);

  }

}