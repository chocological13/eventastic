package com.miniproject.eventastic;

import com.miniproject.eventastic.config.RsaKeyConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyConfigProperties.class)
public class EventasticApplication {

  public static void main(String[] args) {
    SpringApplication.run(EventasticApplication.class, args);
  }

}
