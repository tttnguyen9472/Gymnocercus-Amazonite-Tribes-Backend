package com.greenfoxacademy.springwebapp.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.kingdoms.Location;
import com.greenfoxacademy.springwebapp.models.users.RegisterRequestDTO;
import com.greenfoxacademy.springwebapp.models.users.User;
import com.greenfoxacademy.springwebapp.repositories.KingdomRepository;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import com.greenfoxacademy.springwebapp.security.AuthenticationRequest;
import com.greenfoxacademy.springwebapp.security.PasswordSecurity;
import com.greenfoxacademy.springwebapp.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class SecurityControllerIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  UserService userService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  KingdomRepository kingdomRepository;

  @Test
  public void givenLoginURL_whenMockMVC_thenStatusOK_andReturnsWithUserLoginDTO() throws Exception {
    String encodedPassword = PasswordSecurity.getInstance().encode("LoginPassword");
    userRepository.save(new User("LoginDummyUser",
        kingdomRepository.save(new Kingdom("LoginKingdom", new Location(5, 5))),
        encodedPassword));

    AuthenticationRequest validUser = new AuthenticationRequest("LoginDummyUser", "LoginPassword");

    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(validUser)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("status", is("ok")))
        .andDo(print());
  }

  @Test
  public void givenRegisterAndLoginURL_whenMockMVC_thenCorrectPassword() throws Exception {
    String username = "PasswordDummyUser";
    String password = "password";
    RegisterRequestDTO testRegistrationData = new RegisterRequestDTO(username, password, "PasswordKingdom");
    mockMvc.perform(post("/tribes/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testRegistrationData)))
        .andDo(print());

    AuthenticationRequest validUser = new AuthenticationRequest(username, password);

    mockMvc.perform(post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(validUser)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("status", is("ok")))
        .andDo(print());
  }

}

