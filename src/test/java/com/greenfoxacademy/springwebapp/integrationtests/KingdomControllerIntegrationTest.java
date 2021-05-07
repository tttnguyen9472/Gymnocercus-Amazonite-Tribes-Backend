package com.greenfoxacademy.springwebapp.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.models.buildings.Building;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingType;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.kingdoms.KingdomNameRequestDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.Location;
import com.greenfoxacademy.springwebapp.models.kingdoms.ScoutRequestDTO;
import com.greenfoxacademy.springwebapp.models.resources.Food;
import com.greenfoxacademy.springwebapp.models.resources.Resource;
import com.greenfoxacademy.springwebapp.models.resources.ResourceType;
import com.greenfoxacademy.springwebapp.models.troops.Troop;
import com.greenfoxacademy.springwebapp.models.troops.TroopResponseDTO;
import com.greenfoxacademy.springwebapp.models.users.RegisterRequestDTO;
import com.greenfoxacademy.springwebapp.models.users.User;
import com.greenfoxacademy.springwebapp.repositories.KingdomRepository;
import com.greenfoxacademy.springwebapp.repositories.ResourceRepository;
import com.greenfoxacademy.springwebapp.repositories.TroopRepository;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import com.greenfoxacademy.springwebapp.security.AuthenticationRequest;
import com.greenfoxacademy.springwebapp.security.JwtUtil;
import com.greenfoxacademy.springwebapp.security.MyUserDetailsService;
import com.greenfoxacademy.springwebapp.services.BuildingService;
import com.greenfoxacademy.springwebapp.services.KingdomService;
import com.greenfoxacademy.springwebapp.services.ResourceService;
import com.greenfoxacademy.springwebapp.services.TroopService;
import com.greenfoxacademy.springwebapp.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class KingdomControllerIntegrationTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  KingdomService kingdomService;
  @Autowired
  KingdomRepository kingdomRepository;
  @Autowired
  UserService userService;
  @Autowired
  ResourceService resourceService;
  @Autowired
  BuildingService buildingService;
  @Autowired
  TroopService troopService;
  @Autowired
  JwtUtil jwtUtil;
  @Autowired
  MyUserDetailsService myUserDetailsService;
  @Autowired
  UserRepository userRepository;
  @Autowired
  ResourceRepository resourceRepository;
  @Autowired
  TroopRepository troopRepository;
  @Autowired
  ObjectMapper objectMapper;
  ModelMapper modelMapper;

  private Kingdom setupKingdom(String userName) throws Exception {
    RegisterRequestDTO newUser = new RegisterRequestDTO(userName, "1234", userName + "'s kingdom");
    userService.createUser(newUser);
    Kingdom testKingdom = userService.findByUsername(userName).getKingdom();
    Building testBuilding = buildingService.createBuilding(BuildingType.TOWNHALL);
    testBuilding.setKingdom(testKingdom);
    buildingService.saveBuilding(testBuilding);

    Troop testTroop = troopService.createTroopForKingdom(testKingdom);
    testTroop.setKingdom(testKingdom);
    troopService.saveTroop(testTroop);
    return kingdomService.saveKingdom(testKingdom);
  }

  @Test
  public void givenKingdomList_whenKingdomRepositoryHasTwoKingdom_thenReturnKingdomDTOListWithTwoElement()
      throws Exception {

    kingdomRepository.deleteAll();

    setupKingdom("TestUser1");
    setupKingdom("TestUser2");

    this.mockMvc.perform(get("/tribes/kingdom"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name", is(kingdomService.getAllKingdom().get(0).getName())))
        .andExpect(jsonPath("$[1].user", is(kingdomService.getAllKingdom().get(1).getUser().getUsername())))
        .andDo(print());
  }


  @Test
  public void givenExistingKingdom_whenCallingPostTroop_ReturnNewTroop() throws Exception {
    String username = "TestUser3";
    String password = "password3";
    RegisterRequestDTO testRegisterData = new RegisterRequestDTO(username, password, "Kingdom6");

    Kingdom kingdom = kingdomService.getKingdomById(userService.createUser(testRegisterData).getKingdomId());
    Resource gold = resourceService.getResourceFromList(kingdom, ResourceType.GOLD);
    gold.setAmount(100);
    resourceService.saveResource(gold);
    Resource food = resourceService.getResourceFromList(kingdom, ResourceType.FOOD);
    food.setGeneration(100);
    resourceService.saveResource(food);

    AuthenticationRequest loginData = new AuthenticationRequest(username, password);
    String token = userService.loginUser(loginData).getToken();
    modelMapper = new ModelMapper();
    TroopResponseDTO newTroopResponseDTO =
        modelMapper.map(new Troop(kingdom), TroopResponseDTO.class);

    MvcResult response = mockMvc.perform(post("/tribes/kingdom/troop")
        .header("Amazonite-tribes-token", token))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();

    TroopResponseDTO result =
        objectMapper.readValue(response.getResponse().getContentAsString(), TroopResponseDTO.class);
    assertEquals(result.getMessage(), newTroopResponseDTO.getMessage());
    assertEquals(result.getKingdomId(), newTroopResponseDTO.getKingdomId());
    assertEquals(result.getLevel(), newTroopResponseDTO.getLevel());
  }

  @Test
  public void givenExistingKingdom_whenCallingPUTKingdom_thenReturnNewKingdomName()
      throws Exception {
    String username = "TestUser4";
    String password = "password4";
    RegisterRequestDTO testRegisterData = new RegisterRequestDTO(username, password, "Kingdom5");
    userService.createUser(testRegisterData);
    AuthenticationRequest loginData = new AuthenticationRequest(username, password);
    String token = userService.loginUser(loginData).getToken();

    String newName = "newName";
    KingdomNameRequestDTO request = new KingdomNameRequestDTO(newName);

    HttpHeaders httpHeader = new HttpHeaders();
    httpHeader.add("Amazonite-tribes-token", token);

    mockMvc.perform(put("/tribes/kingdom")
        .headers(httpHeader)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("kingdom_name", is(newName)))
        .andDo(print());
  }

  @Test
  public void givenCorrectToken_WhenMockMvc_ThenReturnResourceList() throws Exception {
    Location mockLocation = new Location(15, 15);
    Kingdom mockKingdom = new Kingdom("KingdomForToken", mockLocation);
    kingdomRepository.save(mockKingdom);

    String mockUsername = "DummyUserForToken";

    User user = new User(mockUsername, mockKingdom);
    user.setKingdom(mockKingdom);

    userRepository.save(user);

    Resource mockFood = new Food();
    mockFood.setKingdom(mockKingdom);
    resourceRepository.save(mockFood);

    UserDetails mockUserDetails = myUserDetailsService.loadUserByUsername(mockUsername);
    String token = jwtUtil.generateToken(mockUserDetails);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);

    mockMvc.perform(get("/tribes/kingdom/resource")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders)
        .characterEncoding("utf-8"))
        .andExpect(status().isOk());
  }

  @Test
  public void givenCorrectTokenAndBuildingID_WhenMockMvc_ThenUpdateBuildingWithSameId() throws Exception {
    Kingdom kingdom = setupKingdom("testUser3");
    User user = kingdom.getUser();
    UserDetails mockUserDetails
        =
        myUserDetailsService.loadUserByUsername(user.getUsername());
    String token = jwtUtil.generateToken(mockUserDetails);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);

    Resource thousandGold = resourceService.getResourceFromList(kingdom, ResourceType.GOLD);
    thousandGold.setAmount(1000);
    resourceService.saveResource(thousandGold);
    Integer buildingLvlBeforeUpgrade =
        buildingService.getBuildingFromList(user.getKingdom(), BuildingType.TOWNHALL).getLevel();
    Long buildingId = buildingService.getBuildingFromList(user.getKingdom(), BuildingType.TOWNHALL).getId();

    mockMvc.perform(put("/tribes/kingdom/building/" + buildingId)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders)
        .characterEncoding("utf-8"))
        .andExpect(jsonPath("$.level", is(buildingLvlBeforeUpgrade + 1)))
        .andExpect(status().isOk());
  }

  @Test
  public void givenCorrectTokenAndEnoughPayment_ThenGetKingdomsInRange() throws Exception {
    kingdomRepository.deleteAll();
    Kingdom kingdom = kingdomRepository.save(new Kingdom("ScoutingKingdom", new Location(150, 150)));

    kingdomRepository.save(new Kingdom("Kingdom1", new Location(100, 100)));
    kingdomRepository.save(new Kingdom("Kingdom2", new Location(651, 651)));
    kingdomRepository.save(new Kingdom("Kingdom3", new Location(200, 800)));
    Kingdom kingdom4 = kingdomRepository.save(new Kingdom("Kingdom4", new Location(150, 600)));
    User user = userService.saveUser(new User("ScoutingUser", kingdom, "password"));

    for (int i = 0; i < 6; i++) {
      troopService.createTroopForKingdom(kingdom4);
    }

    UserDetails mockUserDetails = myUserDetailsService.loadUserByUsername(user.getUsername());
    String token = jwtUtil.generateToken(mockUserDetails);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);
    mockMvc.perform(get("/tribes/kingdom/scout")
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(new ScoutRequestDTO(5)))
        .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].location.x", is(100)))
        .andExpect(jsonPath("$[0].location.y", is(100)))
        .andExpect(jsonPath("$[1].kingdom_name", is("Kingdom4")))
        .andExpect(jsonPath("$[1].troop_number", is("between 5 and 9")))
        .andDo(print());
  }

  @Test
  public void givenCorrectTokenAndTroopID_WhenMockMvc_ThenUpgradeTroopWithSameId() throws Exception {

    Kingdom kingdom = setupKingdom("TestTestTest");

    User user = kingdom.getUser();
    List<Troop> troopList = troopService.getTroopListByKingdom(kingdom);
    Troop troop = troopList.get(0);

    UserDetails mockUserDetails
        =
        myUserDetailsService.loadUserByUsername(user.getUsername());
    String token = jwtUtil.generateToken(mockUserDetails);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);

    Long troopId = troop.getId();

    Integer troopLvlBeforeUpgrade = troopService.getTroopById(troopId).getLevel();

    mockMvc.perform(put("/tribes/kingdom/troop/" + troopId)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders)
        .characterEncoding("utf-8"))
        .andExpect(jsonPath("$.level", is(troopLvlBeforeUpgrade + 1)))
        .andExpect(status().isOk());
  }

  @Test
  public void givenIncorrectToken_WhenMockMvc_ThenReturnErrorForResource() throws Exception {

    String token = "asd.efg.hij";

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);

    mockMvc.perform(get("/tribes/kingdom/resource")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders)
        .characterEncoding("utf-8"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void givenEmptyToken_WhenMockMvc_ThenReturnErrorForResource() throws Exception {

    String token = "";

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);

    mockMvc.perform(get("/tribes/kingdom/resource")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders)
        .characterEncoding("utf-8"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void givenCorrectToken_WhenMockMvc_ThenReturnBuildingList() throws Exception {
    Kingdom testKingdom = kingdomRepository.save(setupKingdom("AsdUserSomething"));

    UserDetails mockUserDetails =
        myUserDetailsService.loadUserByUsername(testKingdom.getUser().getUsername());
    String token = jwtUtil.generateToken(mockUserDetails);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);

    mockMvc.perform(get("/tribes/kingdom/building")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders)
        .characterEncoding("utf-8"))
        .andExpect(status().isOk());
  }

  @Test
  public void givenCorrectToken_WhenMockMvc_ThenReturnTroopList() throws Exception {
    Kingdom testKingdom = kingdomRepository.save(setupKingdom("TestDummyUserSomething"));

    UserDetails mockUserDetails = myUserDetailsService.loadUserByUsername(testKingdom.getUser().getUsername());
    String token = jwtUtil.generateToken(mockUserDetails);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);

    mockMvc.perform(get("/tribes/kingdom/troop")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders)
        .characterEncoding("utf-8"))
        .andExpect(status().isOk());
  }

  @Test
  public void givenCorrectToken_WhenMockMvc_ThenReturnWarResultDTO() throws Exception {
    Kingdom testKingdom = kingdomRepository.save(setupKingdom("TestDummyUserSomething123"));

    UserDetails mockUserDetails = myUserDetailsService.loadUserByUsername(testKingdom.getUser().getUsername());
    String token = jwtUtil.generateToken(mockUserDetails);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);

    Resource gold = resourceService.getResourceFromList(testKingdom, ResourceType.GOLD);
    gold.setAmount(200);
    resourceService.saveResource(gold);
    Resource food = resourceService.getResourceFromList(testKingdom, ResourceType.FOOD);
    food.setAmount(200);
    resourceService.saveResource(food);
    Troop testTroop = troopService.createTroopForKingdom(testKingdom);
    troopService.saveTroop(testTroop);
    Kingdom enemyKingdom = kingdomRepository.save(setupKingdom("TestDummyUserSomething456"));

    mockMvc.perform(get("/tribes/kingdom/attack/{id}", enemyKingdom.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders)
        .characterEncoding("utf-8"))
        .andExpect(status().isOk());
  }
}