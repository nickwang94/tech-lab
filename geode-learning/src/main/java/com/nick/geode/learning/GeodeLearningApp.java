package com.nick.geode.learning;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;

/**
 * Minimal entry point used as a compilation sanity-check.
 *
 * Note: this creates a local Cache instance only (no server/locator required).
 */
public final class GeodeLearningApp {

  private GeodeLearningApp() {}

  public static void main(String[] args) {
    Cache cache = new CacheFactory().create();
    try {
      System.out.println("Geode cache created: " + cache.getName());
    } finally {
      cache.close();
    }
  }
}
