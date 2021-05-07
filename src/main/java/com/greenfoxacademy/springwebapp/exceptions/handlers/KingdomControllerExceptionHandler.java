package com.greenfoxacademy.springwebapp.exceptions.handlers;

import com.greenfoxacademy.springwebapp.exceptions.kingdoms.InsufficientResourceException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.MissingResourcesException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NoSuchKingdomException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughResourceException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotEnoughTroopException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.NotYourBuildingException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.ReservedKingdomnameException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.TownHallLevelException;
import com.greenfoxacademy.springwebapp.exceptions.kingdoms.UnableToAttackYourselfException;
import com.greenfoxacademy.springwebapp.models.TransactionInformationDTO;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingErrorDTO;
import com.greenfoxacademy.springwebapp.models.kingdoms.KingdomErrorDTO;
import com.greenfoxacademy.springwebapp.exceptions.troops.InvalidTroopIdException;
import com.greenfoxacademy.springwebapp.exceptions.troops.NotYourTroopException;
import com.greenfoxacademy.springwebapp.models.TransactionInformationDTO;
import com.greenfoxacademy.springwebapp.models.buildings.BuildingErrorDTO;
import com.greenfoxacademy.springwebapp.models.troops.TroopErrorDTO;
import com.greenfoxacademy.springwebapp.models.users.RegisterResponseDTO;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class KingdomControllerExceptionHandler {

  private static final Logger logger = Logger.getLogger(KingdomControllerExceptionHandler.class);

  @ExceptionHandler(ReservedKingdomnameException.class)
  public ResponseEntity<RegisterResponseDTO> reservedKingdomnameExceptionHandling(ReservedKingdomnameException ex) {
    logger.warn("ReservedKingdomnameException: " + ex.getRequestedName());
    return new ResponseEntity<>(new RegisterResponseDTO("Kingdom name already taken, please choose an other one."),
        HttpStatus.CONFLICT);
  }

  @ExceptionHandler(NoSuchKingdomException.class)
  public ResponseEntity<RegisterResponseDTO> noSuchKingdomExceptionHandling() {
    logger.warn("NoSuchKingdomException");
    return new ResponseEntity<>(new RegisterResponseDTO("Wrong Kingdom ID!"),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotEnoughResourceException.class)
  public ResponseEntity<TransactionInformationDTO> notEnoughResourceExceptionHandling(NotEnoughResourceException ex) {
    logger.warn(ex.getMessage());
    return new ResponseEntity<>(new TransactionInformationDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotYourBuildingException.class)
  public ResponseEntity<BuildingErrorDTO> notYourBuildingExceptionHandling(NotYourBuildingException ex) {
    logger.warn("NotYourBuildingException: " + ex.getBuildingId());
    return new ResponseEntity<>(new BuildingErrorDTO("The specified building does not belong to you."),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TownHallLevelException.class)
  public ResponseEntity<BuildingErrorDTO> townHallLevelExceptionHandling(TownHallLevelException ex) {
    logger.warn("TownHallLevelException. Current level is " + ex.getTownhallLevel());
    return new ResponseEntity<>(new BuildingErrorDTO("The townhall level must be greater than the building level."),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InsufficientResourceException.class)
  public ResponseEntity<BuildingErrorDTO> insufficientResourceExceptionHandling() {
    return new ResponseEntity<>(new BuildingErrorDTO("Insufficient resources."),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnableToAttackYourselfException.class)
  public ResponseEntity<KingdomErrorDTO> unableToAttackYourselfExceptionHandler() {
    return new ResponseEntity<>(new KingdomErrorDTO("Why attacking yourself, bruh?"), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotEnoughTroopException.class)
  public ResponseEntity<KingdomErrorDTO> notEnoughTroopExceptionHandler() {
    return new ResponseEntity<>(new KingdomErrorDTO("You don't have troops to fight! Are you crazy?"),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MissingResourcesException.class)
  public ResponseEntity<KingdomErrorDTO> missingResourcesExceptionHandling(MissingResourcesException ex) {
    String message = "Not enough : " + String.join(", ", ex.getMissingParameterList());
    return new ResponseEntity<>(new KingdomErrorDTO(message), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotYourTroopException.class)
  public ResponseEntity<TroopErrorDTO> notYourTroopExceptionHandling(NotYourTroopException ex) {
    logger.warn("NotYourTroopException: " + ex.getTroopId());
    return new ResponseEntity<>(new TroopErrorDTO("The specified troop does not belong to you."),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidTroopIdException.class)
  public ResponseEntity<TroopErrorDTO> invalidTroopIdExceptionHandling(NotYourTroopException ex) {
    logger.warn("InvalidTroopIdException: " + ex.getTroopId());
    return new ResponseEntity<>(new TroopErrorDTO("The specified troop ID is invalid."),
        HttpStatus.BAD_REQUEST);
  }
}
