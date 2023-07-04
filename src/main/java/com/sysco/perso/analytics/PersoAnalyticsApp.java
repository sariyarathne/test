package com.sysco.perso.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class PersoAnalyticsApp {

  static {
    // Enabling async logging https://logging.apache.org/log4j/2.x/manual/async.html
    System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
  }

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"));
    SpringApplication.run(PersoAnalyticsApp.class, args);
  }

}
