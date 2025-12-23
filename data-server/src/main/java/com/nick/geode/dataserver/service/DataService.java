package com.nick.geode.dataserver.service;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for data operations (put, get, delete) on Geode regions.
 */
@Service
public class DataService {

  /**
   * Get the cache instance directly from CacheFactory to avoid proxy issues.
   */
  private Cache getCache() {
    try {
      GemFireCache cache = CacheFactory.getAnyInstance();
      if (cache != null && !cache.isClosed() && cache instanceof Cache) {
        return (Cache) cache;
      }
    } catch (Exception e) {
      // Cache not available
    }
    return null;
  }

  /**
   * Put a key-value pair into a region.
   */
  public Map<String, Object> putData(String regionName, String key, Object value) {
    Map<String, Object> result = new HashMap<>();
    
    Cache cache = getCache();
    if (cache == null || cache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      Region<Object, Object> region = cache.getRegion(regionName);
      
      if (region == null) {
        result.put("success", false);
        result.put("error", "Region not found: " + regionName);
        return result;
      }

      region.put(key, value);
      
      result.put("success", true);
      result.put("message", "Data put successfully");
      result.put("region", regionName);
      result.put("key", key);
      
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Failed to put data: " + e.getMessage());
    }
    
    return result;
  }

  /**
   * Get a value from a region by key.
   */
  public Map<String, Object> getData(String regionName, String key) {
    Map<String, Object> result = new HashMap<>();
    
    Cache cache = getCache();
    if (cache == null || cache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      Region<Object, Object> region = cache.getRegion(regionName);
      
      if (region == null) {
        result.put("success", false);
        result.put("error", "Region not found: " + regionName);
        return result;
      }

      Object value = region.get(key);
      
      result.put("success", true);
      result.put("region", regionName);
      result.put("key", key);
      result.put("value", value);
      result.put("exists", value != null);
      
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Failed to get data: " + e.getMessage());
    }
    
    return result;
  }

  /**
   * Delete a key from a region.
   */
  public Map<String, Object> deleteData(String regionName, String key) {
    Map<String, Object> result = new HashMap<>();
    
    Cache cache = getCache();
    if (cache == null || cache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      Region<Object, Object> region = cache.getRegion(regionName);
      
      if (region == null) {
        result.put("success", false);
        result.put("error", "Region not found: " + regionName);
        return result;
      }

      Object removedValue = region.remove(key);
      
      result.put("success", true);
      result.put("message", "Data deleted successfully");
      result.put("region", regionName);
      result.put("key", key);
      result.put("removedValue", removedValue);
      
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Failed to delete data: " + e.getMessage());
    }
    
    return result;
  }

  /**
   * Get all keys in a region.
   */
  public Map<String, Object> getAllKeys(String regionName) {
    Map<String, Object> result = new HashMap<>();
    
    Cache cache = getCache();
    if (cache == null || cache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      Region<Object, Object> region = cache.getRegion(regionName);
      
      if (region == null) {
        result.put("success", false);
        result.put("error", "Region not found: " + regionName);
        return result;
      }

      result.put("success", true);
      result.put("region", regionName);
      result.put("keys", region.keySet());
      result.put("size", region.size());
      
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Failed to get keys: " + e.getMessage());
    }
    
    return result;
  }
}

