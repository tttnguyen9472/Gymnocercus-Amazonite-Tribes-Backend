package com.greenfoxacademy.springwebapp.services;


import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;


public class TimeServiceTest {
  private final Timestamp past = new Timestamp(0L);
  private final Timestamp future = new Timestamp(60 * 1000L);

  TimeService timeService = new TimeService() {
    @Override
    Long getCurrentTimeInMillis() {
      return 10 * 60 * 1000L;
    }
  };


  @Test
  public void minutesBetweenTwoTimestamps() {
    Long actualMinutes = timeService.minutesBetweenTwoTimestamps(future, past);
    Long expectedMinutes = 1L;
    assertEquals(expectedMinutes, actualMinutes);
  }

  @Test
  public void minutesBetweenATimestampAndTheActualTime() {
    Long actualMinutes = timeService.minutesBetweenATimestampAndTheActualTime(past);
    Long expectedMinutes = 10L;
    assertEquals(expectedMinutes, actualMinutes);
  }

}