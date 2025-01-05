package com.nick.gemfire.locator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.config.annotation.EnableManager;
import org.springframework.data.gemfire.config.annotation.LocatorApplication;
import org.springframework.geode.config.annotation.UseLocators;

@UseLocators
@SpringBootApplication
@LocatorApplication(name = "SpringBootApacheGeodeLocatorApplication")
public class SpringBootApacheGeodeLocatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApacheGeodeLocatorApplication.class, args);
    }

    @Configuration
    @EnableManager(start = true)
    @Profile("manager")
    @SuppressWarnings("unused")
    static class ManagerConfiguration {}
}
