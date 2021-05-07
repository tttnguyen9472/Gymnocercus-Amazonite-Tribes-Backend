package com.greenfoxacademy.springwebapp.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.controllers.UserController;
import com.greenfoxacademy.springwebapp.exceptions.users.MissingParameterException;
import com.greenfoxacademy.springwebapp.exceptions.users.ReservedUsernameException;
import com.greenfoxacademy.springwebapp.exceptions.handlers.UserControllerExceptionHandler;
import com.greenfoxacademy.springwebapp.models.users.RegisterRequestDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerExceptionHandlerTest {

  private MockMvc mockMvc;
  private UserController mockUserController;
  private ObjectMapper objectMapper;

  @Before
  public void setUp() {
    objectMapper = new ObjectMapper();
    mockUserController = Mockito.mock(UserController.class);

    MockitoAnnotations.initMocks(this);
    mockMvc =
        MockMvcBuilders.standaloneSetup(mockUserController).setControllerAdvice(new UserControllerExceptionHandler())
            .build();
  }

  @Test
  public void reservedUsernameExceptionHandling_HappyCase() throws Exception {
    RegisterRequestDTO testRegistrationData = new RegisterRequestDTO("DummyUser", "1234", "Kingdom");

    when(mockUserController.registerUser(any())).thenThrow(new ReservedUsernameException());

    mockMvc.perform(post("/tribes/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testRegistrationData)))
        .andExpect(status().is(409))
        .andExpect(jsonPath("status", is("error")))
        .andExpect(jsonPath("error", is("Username already taken, please choose an other one.")))
        .andDo(print());
  }

  @Test
  public void missingParameterExceptionHandling_HappyCase() throws Exception {
    RegisterRequestDTO testRegistrationData = new RegisterRequestDTO("DummyUser");

    when(mockUserController.registerUser(any()))
        .thenThrow(new MissingParameterException(Arrays.asList("password", "kingdom")));

    mockMvc.perform(post("/tribes/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testRegistrationData)))
        .andExpect(status().is(400))
        .andExpect(jsonPath("status", is("error")))
        .andExpect(jsonPath("error", is("Missing parameter(s): password, kingdom")))
        .andDo(print());
  }

}