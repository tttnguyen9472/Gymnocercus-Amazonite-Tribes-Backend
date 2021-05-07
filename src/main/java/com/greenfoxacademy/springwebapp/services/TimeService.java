package com.greenfoxacademy.springwebapp.services;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;


@Service
public class TimeService {


  public Long minutesBetweenTwoTimestamps(Timestamp future, Timestamp past) {
    return TimeUnit.MILLISECONDS.toMinutes(future.getTime() - past.getTime());
  }

  public Long minutesBetweenATimestampAndTheActualTime(Timestamp timestamp) {
    return TimeUnit.MILLISECONDS.toMinutes(getCurrentTimeInMillis() - timestamp.getTime());
  }

  Long getCurrentTimeInMillis() {
    return System.currentTimeMillis();
  }

}

