package com.greenfoxacademy.springwebapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughResourceException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.ReservedKingdomnameException;
import com.greenfoxacademy.springwebapp.exceptions.users.MissingParameterException;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingObjectDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.kingdoms.KingdomNameRequestDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.Location;
import com.greenfoxacademy.springwebapp.models.kingdoms.LocationDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.ScoutRequestDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.ScoutResponseDTO;
import com.greenfoxacademy.springwebapp.models.troops.Troop;
import com.greenfoxacademy.springwebapp.models.troops.TroopDTO;
import com.greenfoxacademy.springwebapp.models.troops.TroopResponseDTO;
import com.greenfoxacademy.springwebapp.security.JwtRequestFilter;
import com.greenfoxacademy.springwebapp.security.JwtUtil;
import com.greenfoxacademy.springwebapp.security.MyUserDetailsService;
import com.greenfoxacademy.springwebapp.services.BuildingService;
import com.greenfoxacademy.springwebapp.services.KingdomService;
import com.greenfoxacademy.springwebapp.services.TroopService;
import com.greenfoxacademy.springwebapp.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(KingdomController.class)
@AutoConfigureMockMvc(addFilters = false)
public class KingdomControllerTest {

  @Autowired
  MockMvc mockMvc;
  @MockBean
  KingdomService kingdomService;
  @MockBean
  JwtRequestFilter jwtRequestFilter;
  @MockBean
  MyUserDetailsService myUserDetailsService;
  @MockBean
  UserService userService;
  @MockBean
  BuildingService buildingService;
  @MockBean
  TroopService troopService;
  @MockBean
  JwtUtil jwtUtil;
  @Autowired
  ObjectMapper objectMapper;
  ModelMapper modelMapper;
  HttpHeaders httpHeaders;

  @Before
  public void createRequestHeader() {
    httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", "token.token.token");
  }

  @Test
  public void listAllBuildings_HappyCase() throws Exception {
    List<BuildingObjectDTO> mockBuildingList = new ArrayList<>();
    mockBuildingList.add(new BuildingObjectDTO());
    when(kingdomService.listAllBuildings(any())).thenReturn(mockBuildingList);

    mockMvc.perform(get("/tribes/kingdom/building")
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8")
        .headers(httpHeaders))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  public void listAllTroops_HappyCase() throws Exception {
    List<TroopDTO> mockTroopList = new ArrayList<>();
    mockTroopList.add(new TroopDTO());
    when(kingdomService.listAllTroops(any())).thenReturn(mockTroopList);

    mockMvc.perform(get("/tribes/kingdom/troop")
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding("utf-8")
        .headers(httpHeaders))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andDo(print());
  }

  @Test
  public void addNewTroop_happyCase() throws Exception {
    modelMapper = new ModelMapper();
    Kingdom mockKingdom = new Kingdom(1L, "TestKingdom", new Location(1, 1));
    TroopResponseDTO mockTroopResponseDTO = modelMapper.map(new Troop(mockKingdom), TroopResponseDTO.class);
    mockTroopResponseDTO.setId(2L);

    when(kingdomService.addNewTroopToKingdom(any())).thenReturn(mockTroopResponseDTO);

    MvcResult response = mockMvc.perform(post("/tribes/kingdom/troop")
        .headers(httpHeaders))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();

    TroopResponseDTO result = objectMapper.readValue(
        response.getResponse().getContentAsString(), TroopResponseDTO.class);
    Assert.assertEquals(result, mockTroopResponseDTO);
  }

  @Test
  public void upgradeTroopLevel_HappyCase() throws Exception {
    Troop mockTroop = new Troop();
    TroopDTO troopDTO = new TroopDTO(mockTroop);
    when(troopService.upgradeTroop(any(), any())).thenReturn(troopDTO);
    Long troopId = 1L;
    mockMvc.perform(put("/tribes/kingdom/troop/" + troopId)
        .headers(httpHeaders))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();
  }

  @Test
  public void addNewTroop_notEnoughFood_andGetWarning() throws Exception {
    modelMapper = new ModelMapper();
    Kingdom mockKingdom = new Kingdom(1L, "TestKingdom", new Location(1, 1));
    TroopResponseDTO mockTroopResponseDTO = modelMapper.map(new Troop(mockKingdom), TroopResponseDTO.class);
    String message = "Warning! You have too many troops, your food generation value is negative. "
        + "When the food amount reaches 0, your troops will die!";
    mockTroopResponseDTO.setId(2L);
    mockTroopResponseDTO.setMessage(message);

    when(kingdomService.addNewTroopToKingdom(any())).thenReturn(mockTroopResponseDTO);

    mockMvc.perform(post("/tribes/kingdom/troop")
        .headers(httpHeaders))
        .andExpect(status().isOk())
        .andExpect(jsonPath("message", is(message)))
        .andDo(print());
  }

  @Test
  public void addNewTroop_notEnoughGold_andGetException() throws Exception {
    String token = "token.token.token";
    modelMapper = new ModelMapper();
    Kingdom mockKingdom = new Kingdom(1L, "TestKingdom", new Location(1, 1));
    TroopResponseDTO mockTroopResponseDTO = modelMapper.map(new Troop(mockKingdom), TroopResponseDTO.class);
    mockTroopResponseDTO.setId(2L);

    when(kingdomService.addNewTroopToKingdom(any())).thenThrow(new NotEnoughResourceException("Insufficient gold"));

    mockMvc.perform(post("/tribes/kingdom/troop")
        .headers(httpHeaders))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotEnoughResourceException))
        .andExpect(jsonPath("message", is("Insufficient gold")))
        .andDo(print());
  }

  @Test
  public void renameKingdom_happyCase() throws Exception {
    String newKingdomName = "newName";
    KingdomNameRequestDTO request = new KingdomNameRequestDTO(newKingdomName);

    when(kingdomService.changeKingdomName(any(), any())).thenReturn(newKingdomName);

    mockMvc.perform(put("/tribes/kingdom")
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("kingdom_name", is(newKingdomName)))
        .andDo(print());
  }


  @Test
  public void renameKingdom_whenMissingRequestDTO_thenThrowException() throws Exception {
    KingdomNameRequestDTO request = null;

    when(kingdomService.changeKingdomName(any(), any()))
        .thenThrow(new MissingParameterException(Arrays.asList("name")));

    mockMvc.perform(put("/tribes/kingdom")
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .characterEncoding("utf-8"))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingParameterException))
        .andExpect(jsonPath("error", is("Missing parameter(s): name")))
        .andDo(print());
  }


  @Test
  public void renameKingdom_whenKingdomNameIsOccupied_thenThrowException() throws Exception {
    KingdomNameRequestDTO request = new KingdomNameRequestDTO("oldKingdomName");

    when(kingdomService.changeKingdomName(any(), any())).thenThrow(new ReservedKingdomnameException("oldKingdomName"));

    mockMvc.perform(put("/tribes/kingdom")
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .characterEncoding("utf-8"))
        .andExpect(status().isConflict())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservedKingdomnameException))
        .andExpect(jsonPath("error", is("Kingdom name already taken, please choose an other one.")))
        .andDo(print());
  }


  @Test
  public void scout_happyCase() throws Exception {
    List<ScoutResponseDTO> expectedScoutResponseDTOList =
        Arrays.asList(new ScoutResponseDTO(new LocationDTO(100, 100), 2L, "Kingdom2", "between 0 and 4"),
            new ScoutResponseDTO(new LocationDTO(600, 150), 5L, "Kingdom5", "between 5 and 9"));

    when(kingdomService.scout(any(), any())).thenReturn(expectedScoutResponseDTOList);

    MvcResult response = mockMvc.perform(get("/tribes/kingdom/scout")
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(new ScoutRequestDTO(5)))
        .characterEncoding("utf-8"))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();

    String result = response.getResponse().getContentAsString();
    Assert.assertEquals(result, objectMapper.writeValueAsString(expectedScoutResponseDTOList));
  }

  @Test
  public void scout_whenInputPaymentIsNull_thenThrowException() throws Exception {

    when(kingdomService.scout(any(), any())).thenThrow(new NotEnoughResourceException("You have to pay for scouting!"));

    mockMvc.perform(get("/tribes/kingdom/scout")
        .headers(httpHeaders))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotEnoughResourceException))
        .andExpect(jsonPath("message", is("You have to pay for scouting!")))
        .andDo(print());
  }


}