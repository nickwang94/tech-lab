package com.nick.gemfire.geode.configuration;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;

@Configuration
public class GeodeConfig {
    @Bean
    public ClientCache clientCache() {
        return new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)
                .setPoolSubscriptionEnabled(true)
                .create();
    }

    @Bean(name = "exampleRegion")
    public ClientRegionFactoryBean<String, String> exampleRegion(ClientCache clientCache) {
        ClientRegionFactoryBean<String, String> region = new ClientRegionFactoryBean<>();
        region.setCache(clientCache);
        region.setName("exampleRegion");
        region.setShortcut(ClientRegionShortcut.PROXY);
        return region;
    }
}
