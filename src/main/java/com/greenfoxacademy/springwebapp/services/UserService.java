package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NoSuchKingdomException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotYourBuildingException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.ReservedKingdomnameException;
import com.greenfoxacademy.springwebapp.exceptions.users.MissingParameterException;
import com.greenfoxacademy.springwebapp.exceptions.users.NoSuchUserException;
import com.greenfoxacademy.springwebapp.exceptions.users.ReservedUsernameException;
import com.greenfoxacademy.springwebapp.exceptions.users.UserException;
import com.greenfoxacademy.springwebapp.models.users.RegisterRequestDTO;
import com.greenfoxacademy.springwebapp.models.users.RegisterResponseDTO;
import com.greenfoxacademy.springwebapp.models.users.User;
import com.greenfoxacademy.springwebapp.models.users.UserLoginDTO;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import com.greenfoxacademy.springwebapp.security.AuthenticationRequest;
import com.greenfoxacademy.springwebapp.security.AuthenticationResponse;
import com.greenfoxacademy.springwebapp.security.JwtUtil;
import com.greenfoxacademy.springwebapp.security.MyUserDetailsService;
import com.greenfoxacademy.springwebapp.security.PasswordSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

  UserRepository userRepository;
  KingdomService kingdomService;
  BuildingService buildingService;
  ResourceService resourceService;
  AuthenticationManager authenticationManager;
  MyUserDetailsService userDetailsService;
  JwtUtil jwtTokenUtil;

  @Autowired
  public UserService(UserRepository userRepository,
                     KingdomService kingdomService, BuildingService buildingService,
                     ResourceService resourceService,
                     AuthenticationManager authenticationManager,
                     MyUserDetailsService userDetailsService,
                     JwtUtil jwtTokenUtil) {
    this.userRepository = userRepository;
    this.kingdomService = kingdomService;
    this.buildingService = buildingService;
    this.resourceService = resourceService;
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.jwtTokenUtil = jwtTokenUtil;
  }

  public User findByUsername(String username) {
    if (userRepository.findUserByUsername(username).isPresent()) {
      return userRepository.findUserByUsername(username).get();
    } else {
      return null;
    }
  }

  public List<RegisterResponseDTO> getUserDTOList() {
    return userToDTO((List<User>) userRepository.findAll());
  }

  private List<RegisterResponseDTO> userToDTO(List<User> userList) {
    List<RegisterResponseDTO> registerResponseDTOList = userList.stream()
        .map(user -> new RegisterResponseDTO(user.getId(), user.getUsername(), user.getKingdom().getId()))
        .collect(Collectors.toList());
    return registerResponseDTOList;
  }

  public RegisterResponseDTO createUser(RegisterRequestDTO registrationData)
      throws UserException, ReservedKingdomnameException, NoSuchKingdomException, NotYourBuildingException {
    if (registrationData == null) {
      throw new MissingParameterException(Arrays.asList("username", "password", "kingdom"));
    }
    checkForMissingRegisterParameters(registrationData);
    if (isUsernameOccupied(registrationData.getUsername())) {
      throw new ReservedUsernameException(registrationData.getUsername());
    }
    if (kingdomService.isKingdomnameOccupied(registrationData.getKingdom())) {
      throw new ReservedKingdomnameException(registrationData.getKingdom());
    }
    User user = saveUser(
        new User(registrationData.getUsername(), kingdomService.saveNewKingdom(registrationData.getKingdom())));
    saveEncodedPassword(user, registrationData.getPassword());

    buildingService.initializeBuildings(user);

    resourceService.initializeResources(user);

    return new RegisterResponseDTO(user.getId(), user.getUsername(), user.getKingdom().getId());
  }

  private void saveEncodedPassword(User user, String password) {
    PasswordSecurity passwordSecurity = PasswordSecurity.getInstance();
    user.setPassword(passwordSecurity.encode(password));
    userRepository.save(user);
  }

  private void checkForMissingRegisterParameters(RegisterRequestDTO registerData) throws MissingParameterException {
    List<String> missingParameterList = new ArrayList<>();
    checkIfNullOrEmptyField(registerData.getUsername(), "username", missingParameterList);
    checkIfNullOrEmptyField(registerData.getPassword(), "password", missingParameterList);
    checkIfNullOrEmptyField(registerData.getKingdom(), "kingdom", missingParameterList);
    if (missingParameterList.size() > 0) {
      throw new MissingParameterException(missingParameterList);
    }
  }

  private void checkIfNullOrEmptyField(String inputField, String fieldName, List<String> missingParameterList) {
    if (inputField == null || inputField.equals("")) {
      missingParameterList.add(fieldName);
    }
  }

  private boolean isUsernameOccupied(String username) {
    return userRepository.findAllUsername().contains(username);
  }

  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public Boolean isUsernameMissing(AuthenticationRequest loginRequest) {
    return (loginRequest.getUsername() == null || loginRequest.getUsername().equals(""));
  }

  public Boolean isPasswordMissing(AuthenticationRequest loginRequest) {
    return (loginRequest.getPassword() == null || loginRequest.getPassword().equals(""));
  }

  public UserLoginDTO loginUser(AuthenticationRequest loginRequest) throws UserException {
    if (loginRequest == null || isUsernameMissing(loginRequest) && isPasswordMissing(loginRequest)) {
      throw new MissingParameterException(Arrays.asList("username", "password"));
    }
    checkForMissingLoginParameters(loginRequest);
    final UserDetails userDetails;
    userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
            loginRequest.getPassword()));
    final String jwt = jwtTokenUtil.generateToken(userDetails);
    return new UserLoginDTO(new AuthenticationResponse(jwt));
  }

  private void checkForMissingLoginParameters(AuthenticationRequest loginData) throws MissingParameterException {
    List<String> missingParameterList = new ArrayList<>();
    checkIfNullOrEmptyField(loginData.getUsername(), "username", missingParameterList);
    checkIfNullOrEmptyField(loginData.getPassword(), "password", missingParameterList);
    if (missingParameterList.size() > 0) {
      throw new MissingParameterException(missingParameterList);
    }
  }

  public User getUserByToken(String token) throws NoSuchUserException {
    String username = jwtTokenUtil.extractUsername(token);
    return userRepository.findUserByUsername(username).orElseThrow(NoSuchUserException::new);
  }
}

