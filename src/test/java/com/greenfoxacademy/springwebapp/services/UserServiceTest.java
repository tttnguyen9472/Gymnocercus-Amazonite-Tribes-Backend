package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NoSuchKingdomException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotYourBuildingException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.ReservedKingdomnameException;
import com.greenfoxacademy.springwebapp.exceptions.users.MissingParameterException;
import com.greenfoxacademy.springwebapp.exceptions.users.NoSuchUserException;
import com.greenfoxacademy.springwebapp.exceptions.users.ReservedUsernameException;
import com.greenfoxacademy.springwebapp.exceptions.users.UserException;
import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.kingdoms.Location;
import com.greenfoxacademy.springwebapp.models.users.RegisterRequestDTO;
import com.greenfoxacademy.springwebapp.models.users.RegisterResponseDTO;
import com.greenfoxacademy.springwebapp.models.users.User;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import com.greenfoxacademy.springwebapp.security.JwtUtil;
import com.greenfoxacademy.springwebapp.security.MyUserDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

  private UserService userService;
  private UserRepository mockUserRepository;
  private KingdomService mockKingdomService;
  private BuildingService mockBuildingService;
  private ResourceService mockResourceService;
  AuthenticationManager mockAuthenticationManager;
  MyUserDetailsService userDetailsService;
  private JwtUtil jwtTokenUtil;

  @Before
  public void setUp() {
    mockUserRepository = Mockito.mock(UserRepository.class);
    mockKingdomService = Mockito.mock(KingdomService.class);
    mockBuildingService = Mockito.mock(BuildingService.class);
    mockResourceService = Mockito.mock(ResourceService.class);
    mockAuthenticationManager = Mockito.mock(AuthenticationManager.class);
    userDetailsService = Mockito.mock(MyUserDetailsService.class);
    jwtTokenUtil = Mockito.mock(JwtUtil.class);
    userService =
        new UserService(mockUserRepository, mockKingdomService, mockBuildingService, mockResourceService,
            mockAuthenticationManager,
            userDetailsService, jwtTokenUtil);
  }

  @Test
  public void findByUsername() {
    String username = "testuser";
    User mockUser = new User(username);

    when(mockUserRepository.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));

    User actualUser = userService.findByUsername("testuser");
    assertEquals("testuser", actualUser.getUsername());
  }


  @Test
  public void getAllUsers() {

    Kingdom mockKingdom1 = new Kingdom("Kingdom1", new Location(1, 1));
    mockKingdom1.setId(1L);
    User mockUser1 = new User("DummyUser1", mockKingdom1);
    mockUser1.setId(1L);
    Kingdom mockKingdom2 = new Kingdom("Kingdom2", new Location(2, 2));
    mockKingdom2.setId(2L);
    User mockUser2 = new User("DummyUser2", mockKingdom2);
    mockUser2.setId(2L);
    List<User> mockUserList = Arrays.asList(mockUser1, mockUser2);

    List<RegisterResponseDTO> mockDTOList = Arrays.asList(new RegisterResponseDTO(1L, "DummyUser1", 1L),
        new RegisterResponseDTO(2L, "DummyUser2", 2L));

    when((List<User>) mockUserRepository.findAll()).thenReturn(mockUserList);

    assertEquals(mockDTOList.get(0).getKingdomId(), userService.getUserDTOList().get(0).getKingdomId());
    assertEquals(mockDTOList.get(1).getUsername(), userService.getUserDTOList().get(1).getUsername());
  }

  @Test
  public void createUser_HappyCase()
      throws ReservedKingdomnameException, UserException, NoSuchKingdomException, NotYourBuildingException {
    String username = "DummyUser";
    String password = "password";
    String kingdomname = "DummyKingdom";
    Kingdom mockKingdom = new Kingdom(kingdomname, new Location(1, 1));
    mockKingdom.setId(1L);
    User mockUser = new User(username, mockKingdom, password);
    mockUser.setId(1L);
    RegisterRequestDTO registrationData = new RegisterRequestDTO(username, password, kingdomname);

    when(mockUserRepository.findAllUsername()).thenReturn(Arrays.asList("TestUser", "TestUser1"));
    when(mockKingdomService.isKingdomnameOccupied(kingdomname)).thenReturn(false);
    when(mockKingdomService.saveNewKingdom(registrationData.getKingdom())).thenReturn(mockKingdom);
    when(mockUserRepository.save(any(User.class))).thenReturn(mockUser);

    RegisterResponseDTO mockResponse = new RegisterResponseDTO(1L, username, 1L);

    RegisterResponseDTO response = userService.createUser(registrationData);
    assertEquals(mockResponse, response);
  }

  @Test(expected = ReservedUsernameException.class)
  public void createUser_DupicateUsername_ReservedUsernameExceptionExpected()
      throws UserException, ReservedKingdomnameException, NoSuchKingdomException, NotYourBuildingException {

    String username = "TestUser";
    String password = "password";
    String kingdomname = "DummyKingdom";
    RegisterRequestDTO registrationData = new RegisterRequestDTO(username, password, kingdomname);

    when(mockUserRepository.findAllUsername()).thenReturn(Arrays.asList("TestUser", "TestUser1"));

    userService.createUser(registrationData);
  }

  @Test(expected = ReservedKingdomnameException.class)
  public void createUser_DupicateKingdomname_ReservedKingdomnameExceptionExpected()
      throws UserException, ReservedKingdomnameException, NoSuchKingdomException, NotYourBuildingException {
    String username = "DummyUser";
    String password = "password";
    String kingdomname = "TestKingdom";
    RegisterRequestDTO registrationData = new RegisterRequestDTO(username, password, kingdomname);

    when(mockKingdomService.isKingdomnameOccupied(kingdomname)).thenReturn(true);
    when(mockUserRepository.findAllUsername()).thenReturn(Arrays.asList("TestUser", "TestUser1"));

    userService.createUser(registrationData);
  }

  @Test(expected = MissingParameterException.class)
  public void createUser_MissingParameterExceptionExpected()
      throws UserException, ReservedKingdomnameException, NoSuchKingdomException, NotYourBuildingException {
    String username = "DummyUser";
    RegisterRequestDTO registrationData = new RegisterRequestDTO(username);

    when(mockUserRepository.findAllUsername()).thenReturn(Arrays.asList("TestUser", "TestUser1"));
    when(mockKingdomService.getAllKingdomname()).thenReturn(Arrays.asList("TestKingdom", "TestKingdom1"));

    userService.createUser(registrationData);
  }

  @Test(expected = MissingParameterException.class)
  public void createUser_EmptyStringAsParameter_MissingParameterExceptionExpected()
      throws UserException, ReservedKingdomnameException, NoSuchKingdomException, NotYourBuildingException {
    String username = "DummyUser";
    RegisterRequestDTO registrationData = new RegisterRequestDTO(username);

    when(mockUserRepository.findAllUsername()).thenReturn(Arrays.asList("TestUser", "TestUser1"));
    when(mockKingdomService.getAllKingdomname()).thenReturn(Arrays.asList("TestKingdom", "TestKingdom1"));

    userService.createUser(registrationData);
  }

  @Test(expected = MissingParameterException.class)
  public void createUser_whenNullRegisterData_MissingParameterExceptionExpected()
      throws UserException, ReservedKingdomnameException, NoSuchKingdomException, NotYourBuildingException {
    RegisterRequestDTO registrationData = null;

    userService.createUser(registrationData);
  }

  @Test
  public void getUserByToken() throws NoSuchUserException {
    String mockToken = "asd.asd.asd";
    String mockUsername = "DummyUser1";
    User mockUser = new User(mockUsername);

    when(jwtTokenUtil.extractUsername(mockToken)).thenReturn(mockUsername);
    when(mockUserRepository.findUserByUsername(mockUsername)).thenReturn(Optional.of(mockUser));

    assertEquals(mockUsername, userService.getUserByToken(mockToken).getUsername());
  }
}