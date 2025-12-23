package com.nick.geode.dataserver.geode;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.GemFireCache;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;

@Configuration
@EnableConfigurationProperties(DataServerProperties.class)
public class DataServerConfiguration {

  @Bean
  public EmbeddedDataServerLifecycle embeddedDataServerLifecycle(DataServerProperties properties) {
    return new EmbeddedDataServerLifecycle(properties);
  }

  @Bean
  @DependsOn("embeddedDataServerLifecycle")
  @Lazy
  public GemFireCache gemFireCache() {
    // Wait for cache to be available (lifecycle will start it)
    int retries = 20;
    while (retries > 0) {
      try {
        GemFireCache cache = CacheFactory.getAnyInstance();
        if (cache != null && !cache.isClosed()) {
          return cache;
        }
      } catch (Exception e) {
        // Cache not available yet, wait and retry
      }
      
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Interrupted while waiting for cache", e);
      }
      retries--;
    }
    
    throw new IllegalStateException("Failed to get cache instance. Make sure the data server has started and connected to the locator.");
  }
}

