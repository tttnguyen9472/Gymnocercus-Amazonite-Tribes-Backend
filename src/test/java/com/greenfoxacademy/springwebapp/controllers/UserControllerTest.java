package com.greenfoxacademy.springwebapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.ReservedKingdomnameException;
import com.greenfoxacademy.springwebapp.exceptions.users.MissingParameterException;
import com.greenfoxacademy.springwebapp.exceptions.users.ReservedUsernameException;
import com.greenfoxacademy.springwebapp.models.users.RegisterRequestDTO;
import com.greenfoxacademy.springwebapp.models.users.RegisterResponseDTO;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import com.greenfoxacademy.springwebapp.security.JwtUtil;
import com.greenfoxacademy.springwebapp.security.MyUserDetailsService;
import com.greenfoxacademy.springwebapp.services.KingdomService;
import com.greenfoxacademy.springwebapp.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  @MockBean
  UserService mockUserService;
  @Autowired
  @MockBean
  UserRepository mockUserRepository;
  @Autowired
  @MockBean
  MyUserDetailsService mockMyUserDetailsService;
  @Autowired
  @MockBean
  JwtUtil mockJwtUtil;
  @Autowired
  @MockBean
  KingdomService mockKingdomService;
  @Autowired
  ObjectMapper objectMapper;

  @Test
  public void registerUser_HappyCase() throws Exception {
    RegisterRequestDTO testRegistrationData = new RegisterRequestDTO("DummyUser", "1234", "Kingdom");
    RegisterResponseDTO mockResponse = new RegisterResponseDTO(1L, "DummyUser", 1L);

    when(mockUserService.createUser(any())).thenReturn(mockResponse);

    mockMvc.perform(post("/tribes/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testRegistrationData)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id", is(1)))
        .andExpect(jsonPath("username", is("DummyUser")))
        .andExpect(jsonPath("kingdom_id", is(1)))
        .andDo(print());
  }

  @Test
  public void registerUser_DupicateUsername_ReservedUsernameExceptionExpected() throws Exception {
    RegisterRequestDTO testRegistrationData = new RegisterRequestDTO("DummyUser", "1234", "Kingdom");

    when(mockUserService.createUser(any())).thenThrow(new ReservedUsernameException());

    mockMvc.perform(post("/tribes/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testRegistrationData)))
        .andExpect(status().isConflict())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservedUsernameException));
  }

  @Test
  public void registerUser_DupicateKingdomName_ReservedKingdomnameExceptionExpected() throws Exception {
    RegisterRequestDTO testRegistrationData = new RegisterRequestDTO("DummyUser", "1234", "Kingdom");

    when(mockUserService.createUser(any())).thenThrow(new ReservedKingdomnameException("Kingdom"));

    mockMvc.perform(post("/tribes/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testRegistrationData)))
        .andExpect(status().isConflict())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ReservedKingdomnameException));
  }

  @Test
  public void registerUser_MissingParameters_MissingParametersExceptionExpected() throws Exception {
    RegisterRequestDTO testRegistrationData = new RegisterRequestDTO("DummyUser");

    when(mockUserService.createUser(any()))
        .thenThrow(new MissingParameterException(Arrays.asList("password", "kingdom")));

    mockMvc.perform(post("/tribes/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testRegistrationData)))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingParameterException));
  }
}