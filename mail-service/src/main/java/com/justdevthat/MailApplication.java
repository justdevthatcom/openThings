package com.justdevthat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MailApplication {
  public static void main(String[] args) {
    SpringApplication.run(MailApplication.class, args);
  }
}
