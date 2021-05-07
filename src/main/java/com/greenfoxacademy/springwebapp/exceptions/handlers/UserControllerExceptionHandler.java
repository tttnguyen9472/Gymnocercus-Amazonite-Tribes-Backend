package com.greenfoxacademy.springwebapp.exceptions.handlers;

import com.greenfoxacademy.springwebapp.exceptions.users.MissingParameterException;
import com.greenfoxacademy.springwebapp.exceptions.users.ReservedUsernameException;
import com.greenfoxacademy.springwebapp.models.users.RegisterResponseDTO;
import com.greenfoxacademy.springwebapp.models.users.UserLoginDTO;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserControllerExceptionHandler {

  private static final Logger logger = Logger.getLogger(UserControllerExceptionHandler.class);

  @ExceptionHandler(ReservedUsernameException.class)
  public ResponseEntity<RegisterResponseDTO> reservedUsernameExceptionHandling(ReservedUsernameException ex) {
    logger.warn("ReservedUsernameException: " + ex.getRequestedName());
    return new ResponseEntity<>(new RegisterResponseDTO("Username already taken, please choose an other one."),
        HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MissingParameterException.class)
  public ResponseEntity<RegisterResponseDTO> missingParameterExceptionHandling(MissingParameterException ex) {
    logger.warn("MissingParameterException: " + ex.getMissingParameterList());
    String message = "Missing parameter(s): " + String.join(", ", ex.getMissingParameterList());
    return new ResponseEntity<>(new RegisterResponseDTO(message), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<UserLoginDTO> usernameNotFoundExceptionHandling() {
    logger.warn("UsernameNotFoundException");
    return new ResponseEntity<>(new UserLoginDTO("No such user can be found!"), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<UserLoginDTO> badCredentialsExceptionHandling() {
    logger.warn("BadCredentialsException - wrong password");
    return new ResponseEntity<>(new UserLoginDTO("Wrong password!"), HttpStatus.UNAUTHORIZED);
  }

}