package com.nick.geode.locator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class LocatorApplication {

  public static void main(String[] args) {
    // Start Spring Boot application (automatically registers shutdown hook)
    SpringApplication.run(LocatorApplication.class, args);
    
    // Use CountDownLatch to keep the application running
    CountDownLatch latch = new CountDownLatch(1);
    
    // Register shutdown hook to release the latch
    // Spring Boot already registers a shutdown hook to close the context
    Runtime.getRuntime().addShutdownHook(new Thread(latch::countDown));
    
    // Keep the application running until shutdown signal
    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
