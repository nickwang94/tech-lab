package com.nick.geode.locatorlearning;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "geode.locator.enabled=false")
class LocatorLearningApplicationTest {

  @Test
  void contextLoads() {
    // Verifies that the Spring context can start without running an actual Locator.
  }
}
