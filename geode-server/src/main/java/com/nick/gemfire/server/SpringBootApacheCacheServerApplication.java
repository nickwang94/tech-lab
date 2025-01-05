package com.nick.gemfire.server;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.config.annotation.CacheServerApplication;
import org.springframework.data.gemfire.config.annotation.EnableLocator;
import org.springframework.data.gemfire.config.annotation.EnableManager;
import org.springframework.geode.config.annotation.UseLocators;

@SpringBootApplication
@CacheServerApplication(name = "SpringBootApacheCacheServerApplication")
@SuppressWarnings("unused")
public class SpringBootApacheCacheServerApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringBootApacheCacheServerApplication.class)
                .web(WebApplicationType.NONE)
                .build()
                .run(args);
    }

    @Configuration
    @UseLocators
    @Profile("clustered")
    static class ClusteredConfiguration {}

    @Configuration
    @EnableLocator
    @EnableManager(start = true)
    @Profile("!clustered")
    static class LonerConfiguration {}
}
