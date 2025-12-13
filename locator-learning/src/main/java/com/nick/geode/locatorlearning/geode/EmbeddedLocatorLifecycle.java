package com.nick.geode.locatorlearning.geode;

import org.apache.geode.distributed.LocatorLauncher;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Starts and stops an embedded Apache Geode Locator with the Spring application lifecycle.
 */
public class EmbeddedLocatorLifecycle implements SmartLifecycle {

  private final LocatorProperties properties;
  private final AtomicBoolean running = new AtomicBoolean(false);

  private LocatorLauncher launcher;

  public EmbeddedLocatorLifecycle(LocatorProperties properties) {
    this.properties = properties;
  }

  @Override
  public void start() {
    if (!properties.isEnabled()) {
      return;
    }
    if (running.compareAndSet(false, true)) {
      launcher = new LocatorLauncher.Builder()
          .setMemberName(properties.getMemberName())
          .setPort(properties.getPort())
          .setWorkingDirectory(properties.getWorkingDir())
          .build();

      launcher.start();
    }
  }

  @Override
  public void stop() {
    if (running.compareAndSet(true, false) && launcher != null) {
      launcher.stop();
      launcher = null;
    }
  }

  @Override
  public boolean isRunning() {
    return running.get();
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public int getPhase() {
    return Integer.MIN_VALUE;
  }
}
