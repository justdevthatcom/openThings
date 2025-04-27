package com.justdevthat.configuration;

import com.justdevthat.utils.CryptoTool;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeConfiguration {

  @Value("${salt}")
  private String salt;

  @Bean
  public CryptoTool getCryptoTool() {
    return new CryptoTool(salt);
  }
}
