package com.greenfoxacademy.springwebapp.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.controllers.KingdomController;
import com.greenfoxacademy.springwebapp.exceptions.handlers.KingdomControllerExceptionHandler;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.MissingResourcesException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughResourceException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughTroopException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotYourBuildingException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.ReservedKingdomnameException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.TownHallLevelException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.UnableToAttackYourselfException;
import com.greenfoxacademy.springwebapp.models.kingdoms.KingdomNameRequestDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class KingdomControllerExceptionHandlerTest {

  private MockMvc mockMvc;
  private KingdomController mockKingdomController;
  private ObjectMapper objectMapper;

  @Before
  public void setUp() {
    objectMapper = new ObjectMapper();
    mockKingdomController = Mockito.mock(KingdomController.class);

    MockitoAnnotations.initMocks(this);
    mockMvc =
        MockMvcBuilders.standaloneSetup(mockKingdomController)
            .setControllerAdvice(new KingdomControllerExceptionHandler())
            .build();
  }

  @Test
  public void reservedKingdomnameExceptionHandling_HappyCase()
      throws Exception {

    KingdomNameRequestDTO request = new KingdomNameRequestDTO("oldKingdomName");
    String token = "token.token.token";

    when(mockKingdomController.changeKingdomName(any(), any()))
        .thenThrow(new ReservedKingdomnameException("oldKingdomName"));

    HttpHeaders httpHeader = new HttpHeaders();
    httpHeader.add("Amazonite-tribes-token", token);

    mockMvc.perform(put("/tribes/kingdom")
        .headers(httpHeader)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .characterEncoding("utf-8"))
        .andExpect(status().isConflict())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservedKingdomnameException))
        .andExpect(jsonPath("error", is("Kingdom name already taken, please choose an other one.")))
        .andDo(print());
  }

  @Test
  public void notYourBuildingExceptionExceptionHandling_HappyCase() throws Exception {
    String token = "token.token.token";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);
    when(mockKingdomController.updateBuildingDetails(any(), any())).thenThrow(new NotYourBuildingException());

    mockMvc.perform(put("/tribes/kingdom/building/1")
        .contentType(MediaType.APPLICATION_JSON).headers(httpHeaders).characterEncoding("utf-8"))
        .andExpect(status().is(400))
        .andExpect(jsonPath("message", is("The specified building does not belong to you.")))
        .andDo(print());
  }

  @Test
  public void townHallLevelExceptionHandling_HappyCase() throws Exception {
    String token = "token.token.token";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);
    when(mockKingdomController.updateBuildingDetails(any(), any())).thenThrow(new TownHallLevelException());

    mockMvc.perform(put("/tribes/kingdom/building/1")
        .contentType(MediaType.APPLICATION_JSON).headers(httpHeaders).characterEncoding("utf-8"))
        .andExpect(status().is(400))
        .andExpect(jsonPath("message", is("The townhall level must be greater than the building level.")))
        .andDo(print());
  }

  @Test
  public void notEnoughResourceExceptionHandling_HappyCase() throws Exception {
    String token = "token.token.token";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);
    when(mockKingdomController.updateBuildingDetails(any(), any())).thenThrow(new NotEnoughResourceException());

    mockMvc.perform(put("/tribes/kingdom/building/1")
        .contentType(MediaType.APPLICATION_JSON).headers(httpHeaders).characterEncoding("utf-8"))
        .andExpect(status().is(400))
        .andExpect(jsonPath("message", is("Insufficient resources.")))
        .andDo(print());
  }

  @Test
  public void notEnoughTroopExceptionHandling_HappyCase() throws Exception {
    String token = "token.token.token";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);
    when(mockKingdomController.goingToWar(any(), any())).thenThrow(new NotEnoughTroopException());

    mockMvc.perform(get("/tribes/kingdom/attack/1")
        .contentType(MediaType.APPLICATION_JSON).headers(httpHeaders).characterEncoding("utf-8"))
        .andExpect(status().is(400))
        .andExpect(jsonPath("error", is("You don't have troops to fight! Are you crazy?")))
        .andDo(print());
  }

  @Test
  public void unableToAttackYourselfExceptionHandling_HappyCase() throws Exception {
    String token = "token.token.token";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);
    when(mockKingdomController.goingToWar(any(), any())).thenThrow(new UnableToAttackYourselfException());

    mockMvc.perform(get("/tribes/kingdom/attack/1")
        .contentType(MediaType.APPLICATION_JSON).headers(httpHeaders).characterEncoding("utf-8"))
        .andExpect(status().is(400))
        .andExpect(jsonPath("error", is("Why attacking yourself, bruh?")))
        .andDo(print());
  }

  @Test
  public void missingResourcesExceptionHandlingForAttack_HappyCase() throws Exception {
    String token = "token.token.token";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Amazonite-tribes-token", token);
    when(mockKingdomController.goingToWar(any(), any()))
        .thenThrow(new MissingResourcesException(Arrays.asList("gold", "food")));

    mockMvc.perform(get("/tribes/kingdom/attack/1")
        .contentType(MediaType.APPLICATION_JSON).headers(httpHeaders).characterEncoding("utf-8"))
        .andExpect(status().is(400))
        .andExpect(jsonPath("error", is("Not enough : gold, food")))
        .andDo(print());
  }
}