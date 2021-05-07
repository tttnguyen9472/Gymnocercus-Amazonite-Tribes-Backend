package com.greenfoxacademy.springwebapp.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordSecurity implements PasswordEncoder {

  public static PasswordSecurity getInstance() {
    return new PasswordSecurity();
  }

  @Override
  public String encode(CharSequence rawPassword) {
    String encodedPassword = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] bytes = md.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
      encodedPassword = bytesToHex(bytes);
    } catch (NoSuchAlgorithmException e) {
      System.out.println("NoSuchAlgorithmException exception");
      e.printStackTrace();
    }
    return encodedPassword;
  }

  private String bytesToHex(byte[] input) {
    StringBuilder hexString = new StringBuilder(2 * input.length);
    for (byte b : input) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return encode(rawPassword).equals(encodedPassword);
  }
}
