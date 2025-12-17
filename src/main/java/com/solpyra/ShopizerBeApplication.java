package com.solpyra;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShopizerBeApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT+07:00"));
    SpringApplication.run(ShopizerBeApplication.class, args);
  }

}
