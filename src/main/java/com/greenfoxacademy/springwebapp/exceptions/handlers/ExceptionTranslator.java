package com.greenfoxacademy.springwebapp.exceptions.handlers;

import com.greenfoxacademy.springwebapp.security.ErrorEntityDTO;
import io.jsonwebtoken.JwtException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionTranslator {

  private static final Logger logger = Logger.getLogger(KingdomControllerExceptionHandler.class);

  @ExceptionHandler(JwtException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorEntityDTO processJwtException(JwtException ex) {
    logger.error(ex.getMessage());
    logger.debug(ex);
    return new ErrorEntityDTO(HttpStatus.UNAUTHORIZED, "Unauthorized request.");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorEntityDTO processIllegalArgumentException(IllegalArgumentException ex) {
    if (ex.getMessage().equals("JWT String argument cannot be null or empty.")) {
      logger.error(ex.getMessage());
      logger.debug(ex);
      return new ErrorEntityDTO(HttpStatus.UNAUTHORIZED, "JWT String argument cannot be null or empty.");
    }
    logger.warn(ex.getMessage());
    logger.debug(ex);
    throw ex;
  }

}