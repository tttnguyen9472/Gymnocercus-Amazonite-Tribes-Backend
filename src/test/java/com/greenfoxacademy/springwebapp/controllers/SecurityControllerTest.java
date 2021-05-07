package com.greenfoxacademy.springwebapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.exceptions.users.MissingParameterException;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import com.greenfoxacademy.springwebapp.security.AuthenticationRequest;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SecurityController.class)
public class SecurityControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  @MockBean
  UserService mockUserService;

  @Autowired
  @MockBean
  AuthenticationManager mockAuthenticationManager;

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
  KingdomService kingdomService;

  @Test
  public void loginUser_InvalidUser() throws Exception {
    AuthenticationRequest invalidUser = new AuthenticationRequest();
    invalidUser.setUsername("DummyUser");
    invalidUser.setPassword("1234");

    when(mockUserService.loginUser(any())).thenThrow(
        new UsernameNotFoundException("No such user can be found!"));

    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidUser)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("status", is("error")))
        .andExpect(jsonPath("error", is("No such user can be found!")));
  }

  @Test
  public void loginUser_MissingPassword() throws Exception {
    AuthenticationRequest noPasswordUser = new AuthenticationRequest();
    noPasswordUser.setUsername("DummyUser2");
    noPasswordUser.setPassword("");

    when(mockUserService.loginUser(any())).thenThrow(
        new MissingParameterException(Arrays.asList("password")));

    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(noPasswordUser)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("status", is("error")))
        .andExpect(jsonPath("error", is("Missing parameter(s): password")));
  }

  @Test
  public void loginUser_MissingUsername() throws Exception {

    AuthenticationRequest noUsernameUser = new AuthenticationRequest();
    noUsernameUser.setUsername("");
    noUsernameUser.setPassword("1234");

    when(mockUserService.loginUser(any())).thenThrow(
        new MissingParameterException(Arrays.asList("username")));

    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(noUsernameUser)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("status", is("error")))
        .andExpect(jsonPath("error", is("Missing parameter(s): username")));
  }

  @Test
  public void loginUser_MissingUsernameAndPassword() throws Exception {

    AuthenticationRequest emptyUser = new AuthenticationRequest();
    emptyUser.setUsername("");
    emptyUser.setPassword("");

    when(mockUserService.loginUser(any())).thenThrow(
        new MissingParameterException(Arrays.asList("username", "password")));

    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(emptyUser)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("status", is("error")))
        .andExpect(jsonPath("error", is("Missing parameter(s): username, password")));
  }

  @Test
  public void loginUser_UsernameAndPasswordAsNull() throws Exception {
    AuthenticationRequest nullUser = new AuthenticationRequest();

    when(mockUserService.loginUser(any())).thenThrow(
        new MissingParameterException(Arrays.asList("username", "password")));

    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(nullUser)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("status", is("error")))
        .andExpect(jsonPath("error", is("Missing parameter(s): username, password")));
  }

  @Test
  public void loginUser_emptyRequestBody() throws Exception {

    when(mockUserService.loginUser(any())).thenThrow(
        new MissingParameterException(Arrays.asList("username", "password")));

    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("status", is("error")))
        .andExpect(jsonPath("error", is("Missing parameter(s): username, password")));
  }

  @Test
  public void loginUser_WrongPassword() throws Exception {
    AuthenticationRequest wrongPasswordUser = new AuthenticationRequest();
    wrongPasswordUser.setUsername("DummyUser");
    wrongPasswordUser.setPassword("wrongPassword");

    when(mockUserService.loginUser(any())).thenThrow(
        new BadCredentialsException("Wrong password!"));

    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(wrongPasswordUser)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("status", is("error")))
        .andExpect(jsonPath("error", is("Wrong password!")));
  }
}

