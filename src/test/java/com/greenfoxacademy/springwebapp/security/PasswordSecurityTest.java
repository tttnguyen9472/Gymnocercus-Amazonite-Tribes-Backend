package com.greenfoxacademy.springwebapp.security;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PasswordSecurityTest {

  private String password;
  private String encodedPassword;
  private PasswordSecurity testPasswordSecurity;

  @Before
  public void setup() {
    password = "password";
    encodedPassword = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
    testPasswordSecurity = PasswordSecurity.getInstance();
  }

  @Test
  public void checkPasswordEncoding_happyCase() {
    assertEquals(encodedPassword, testPasswordSecurity.encode(password));
  }

  @Test
  public void checkPasswordValidation_happyCase() {
    assertTrue(testPasswordSecurity.matches(password, encodedPassword));
  }

  @Test
  public void checkPasswordValidation_falseCase() {
    assertFalse(testPasswordSecurity.matches("password2", encodedPassword));
  }
}
