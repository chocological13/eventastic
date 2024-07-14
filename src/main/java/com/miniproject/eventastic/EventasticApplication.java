package com.miniproject.eventastic;

import com.miniproject.eventastic.config.EnvConfigurationProperties;
import com.miniproject.eventastic.config.RsaKeyConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//@EnableConfigurationProperties(RsaKeyConfigProperties.class)
@EnableConfigurationProperties({RsaKeyConfigProperties.class, EnvConfigurationProperties.class})
public class EventasticApplication {

  public static void main(String[] args) {
    SpringApplication.run(EventasticApplication.class, args);
  }

}
