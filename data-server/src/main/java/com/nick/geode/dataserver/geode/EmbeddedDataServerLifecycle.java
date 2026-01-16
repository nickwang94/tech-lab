package com.nick.geode.dataserver.geode;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.distributed.ServerLauncher;
import org.springframework.context.SmartLifecycle;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Starts and stops an embedded Apache Geode Data Server with the Spring application lifecycle.
 */
public class EmbeddedDataServerLifecycle implements SmartLifecycle {

  private final DataServerProperties properties;
  private final AtomicBoolean running = new AtomicBoolean(false);

  private ServerLauncher launcher;
  private GemFireCache cache;

  public EmbeddedDataServerLifecycle(DataServerProperties properties) {
    this.properties = properties;
  }

  @Override
  public void start() {
    if (!properties.isEnabled()) {
      return;
    }
    if (running.compareAndSet(false, true)) {
      String workingDir = resolveWorkingDirectory(properties.getWorkingDir());
      ensureDirectoryExists(workingDir);

      String locators = properties.getLocatorHost() + "[" + properties.getLocatorPort() + "]";

      launcher = new ServerLauncher.Builder()
          .setMemberName(properties.getMemberName())
          .setServerPort(0) // Use random port
          .setWorkingDirectory(workingDir)
          .set("locators", locators)
          .set("log-level", "info")
          .build();

      launcher.start();

      // Wait for cache to be available
      int retries = 10;
      while (retries > 0 && cache == null) {
        try {
          cache = CacheFactory.getAnyInstance();
          if (cache == null) {
            try {
              Thread.sleep(500);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              throw new IllegalStateException("Interrupted while waiting for cache", e);
            }
            retries--;
          }
        } catch (Exception e) {
          try {
            Thread.sleep(500);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for cache", ie);
          }
          retries--;
        }
      }
      
      if (cache == null) {
        throw new IllegalStateException("Failed to get cache instance after starting server");
      }
    }
  }

  /**
   * Resolves the working directory path. If the path is relative, it will be resolved
   * relative to the project root (where target directory exists).
   */
  private String resolveWorkingDirectory(String workingDir) {
    Path path = Paths.get(workingDir);
    if (path.isAbsolute()) {
      return workingDir;
    }

    // Try to find project root by looking for target directory
    String userDir = System.getProperty("user.dir");
    Path currentPath = Paths.get(userDir).toAbsolutePath();

    // Walk up the directory tree to find the project root
    Path projectRoot = currentPath;
    while (!projectRoot.resolve("target").toFile().exists()) {
      Path parent = projectRoot.getParent();
      if (parent == null || parent.equals(projectRoot)) {
        // If we can't find target, use current directory
        projectRoot = currentPath;
        break;
      }
      projectRoot = parent;
    }

    // projectRoot is guaranteed to be non-null here
    return projectRoot.resolve(workingDir).toAbsolutePath().toString();
  }

  /**
   * Ensures the working directory exists, creating it if necessary.
   */
  private void ensureDirectoryExists(String workingDir) {
    File dir = new File(workingDir);
    if (!dir.exists()) {
      boolean created = dir.mkdirs();
      if (!created && !dir.exists()) {
        throw new IllegalStateException("Failed to create working directory: " + workingDir);
      }
    }
  }

  @Override
  public void stop() {
    if (running.compareAndSet(true, false)) {
      if (cache != null) {
        cache.close();
        cache = null;
      }
      if (launcher != null) {
        launcher.stop();
        launcher = null;
      }
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

  public GemFireCache getCache() {
    return cache;
  }
}

