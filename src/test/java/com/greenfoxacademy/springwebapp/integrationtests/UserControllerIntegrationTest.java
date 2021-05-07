package com.greenfoxacademy.springwebapp.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.kingdoms.Location;
import com.greenfoxacademy.springwebapp.models.users.RegisterRequestDTO;
import com.greenfoxacademy.springwebapp.models.users.User;
import com.greenfoxacademy.springwebapp.repositories.KingdomRepository;
import com.greenfoxacademy.springwebapp.repositories.LocationRepository;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  UserRepository userRepository;
  @Autowired
  KingdomRepository kingdomRepository;
  @Autowired
  LocationRepository locationRepository;
  @Autowired
  ObjectMapper objectMapper;


  @Test
  public void givenUserURL_whenMockMVC_thenStatusOK_andReturnsWithUserList() throws Exception {

    userRepository.deleteAll();
    kingdomRepository.deleteAll();
    locationRepository.deleteAll();

    userRepository.save(new User("DummyUser1",
        kingdomRepository.save(new Kingdom("Kingdom1", new Location(1, 1))),
        "password"));
    userRepository.save(new User("DummyUser2",
        kingdomRepository.save(new Kingdom("Kingdom2", new Location(2, 2))),
        "password"));
    userRepository.save(new User("DummyUser3",
        kingdomRepository.save(new Kingdom("Kingdom3", new Location(3, 3))),
        "password"));

    mockMvc.perform(get("/tribes/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username", is("DummyUser1")))
        .andExpect(jsonPath("$[1].username", is("DummyUser2")))
        .andExpect(jsonPath("$[2].username", is("DummyUser3")))
        .andDo(print());
  }

  @Test
  public void givenRegisterURL_whenMockMVC_thenStatusOK_andReturnsWithUserResponseDTO() throws Exception {
    RegisterRequestDTO testRegistrationData = new RegisterRequestDTO(
        "RegisterDummyUser", "password", "RegisterKingdom");
    mockMvc.perform(post("/tribes/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testRegistrationData)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("username", is("RegisterDummyUser")))
        .andDo(print());
  }


}
