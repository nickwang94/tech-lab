package com.nick.geode.locatorlearning.geode;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LocatorProperties.class)
public class LocatorLearningConfiguration {

  @Bean
  public EmbeddedLocatorLifecycle embeddedLocatorLifecycle(LocatorProperties properties) {
    return new EmbeddedLocatorLifecycle(properties);
  }
}
