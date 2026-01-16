package com.nick.geode.databrowser.config;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(DataBrowserProperties.class)
public class GemFireClientConfiguration {

  private ClientCache clientCache;

  @Bean
  public ClientCache clientCache(DataBrowserProperties properties) {
    if (clientCache != null && !clientCache.isClosed()) {
      return clientCache;
    }

    // Create client cache and connect to locator
    ClientCacheFactory factory = new ClientCacheFactory();
    factory.addPoolLocator(properties.getLocatorHost(), properties.getLocatorPort());
    factory.setPoolSubscriptionEnabled(true);
    factory.set("name", properties.getMemberName());
    
    // Set log level
    Properties gemfireProperties = new Properties();
    gemfireProperties.setProperty("log-level", "config");
    
    clientCache = factory.set("cache-xml-file", "")
        .create();
    
    return clientCache;
  }

  @PreDestroy
  public void closeCache() {
    if (clientCache != null && !clientCache.isClosed()) {
      clientCache.close();
    }
  }
}
