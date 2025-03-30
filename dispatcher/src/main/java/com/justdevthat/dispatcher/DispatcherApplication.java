package com.justdevthat.dispatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DispatcherApplication {

  private static final Logger logger = LogManager.getLogger(DispatcherApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(DispatcherApplication.class, args);

    //Test logging level
    logger.trace("Trace test message");
    logger.debug("Debug test message");
    logger.info("Info test message");
    logger.warn("Warning test message");
    logger.error("Error test message");
  }
}
