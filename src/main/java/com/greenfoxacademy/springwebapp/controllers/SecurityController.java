package com.greenfoxacademy.springwebapp.controllers;

import com.greenfoxacademy.springwebapp.exceptions.users.UserException;
import com.greenfoxacademy.springwebapp.models.users.UserLoginDTO;
import com.greenfoxacademy.springwebapp.security.AuthenticationRequest;
import com.greenfoxacademy.springwebapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SecurityController {

  UserService userService;

  @Autowired
  public SecurityController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/login")
  public ResponseEntity<UserLoginDTO> login(@RequestBody(required = false) AuthenticationRequest loginRequest) throws
      UserException {
    return ResponseEntity.ok(userService.loginUser(loginRequest));
  }
}