package com.nick.geode.locator.geode;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LocatorProperties.class)
public class LocatorConfiguration {

  @Bean
  public EmbeddedLocatorLifecycle embeddedLocatorLifecycle(LocatorProperties properties) {
    return new EmbeddedLocatorLifecycle(properties);
  }
}
