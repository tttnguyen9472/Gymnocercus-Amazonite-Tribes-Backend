package com.greenfoxacademy.springwebapp.controllers;

import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NoSuchKingdomException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotYourBuildingException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.ReservedKingdomnameException;
import com.greenfoxacademy.springwebapp.exceptions.users.UserException;
import com.greenfoxacademy.springwebapp.models.users.RegisterRequestDTO;
import com.greenfoxacademy.springwebapp.models.users.RegisterResponseDTO;
import com.greenfoxacademy.springwebapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tribes")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/user")
  public ResponseEntity<List<RegisterResponseDTO>> getUsers() {
    return ResponseEntity.ok(userService.getUserDTOList());
  }

  @PostMapping("/register")
  public ResponseEntity<RegisterResponseDTO> registerUser(
      @RequestBody(required = false) RegisterRequestDTO registrationData)
      throws UserException, ReservedKingdomnameException, NoSuchKingdomException, NotYourBuildingException {
    return ResponseEntity.ok(userService.createUser(registrationData));
  }

}