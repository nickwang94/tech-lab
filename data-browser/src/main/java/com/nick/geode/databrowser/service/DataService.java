package com.nick.geode.databrowser.service;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for data operations (put, get, delete, query) on Geode regions.
 */
@Service
public class DataService {

  private final ClientCache clientCache;

  @Autowired
  public DataService(ClientCache clientCache) {
    this.clientCache = clientCache;
  }

  /**
   * Get a value from a region by key.
   */
  public Map<String, Object> getData(String regionName, String key) {
    Map<String, Object> result = new HashMap<>();
    
    if (clientCache == null || clientCache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      Region<Object, Object> region = clientCache.getRegion(regionName);
      
      // If region is not proxied, try to create a proxy
      if (region == null) {
        try {
          region = clientCache.createClientRegionFactory(
              org.apache.geode.cache.client.ClientRegionShortcut.PROXY)
              .create(regionName);
        } catch (Exception e) {
          result.put("success", false);
          result.put("error", "Region not found and could not create proxy: " + regionName);
          return result;
        }
      }

      Object value = region.get(key);
      
      result.put("success", true);
      result.put("region", regionName);
      result.put("key", key);
      result.put("value", value);
      result.put("valueType", value != null ? value.getClass().getName() : null);
      result.put("exists", value != null);
      
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Failed to get data: " + e.getMessage());
    }
    
    return result;
  }

  /**
   * Get all keys in a region.
   */
  public Map<String, Object> getAllKeys(String regionName) {
    Map<String, Object> result = new HashMap<>();
    
    if (clientCache == null || clientCache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      Region<Object, Object> region = clientCache.getRegion(regionName);
      
      // If region is not proxied, try to create a proxy
      if (region == null) {
        try {
          region = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY)
              .create(regionName);
        } catch (Exception e) {
          result.put("success", false);
          result.put("error", "Region not found and could not create proxy: " + regionName);
          return result;
        }
      }

      List<Object> keys = region.keySet().stream().collect(Collectors.toList());
      
      result.put("success", true);
      result.put("region", regionName);
      result.put("keys", keys);
      result.put("size", region.size());
      
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Failed to get keys: " + e.getMessage());
    }
    
    return result;
  }

  /**
   * Get all key-value pairs in a region (with pagination support).
   */
  public Map<String, Object> getAllData(String regionName, Integer limit, Integer offset) {
    Map<String, Object> result = new HashMap<>();
    
    if (clientCache == null || clientCache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      Region<Object, Object> region = clientCache.getRegion(regionName);
      
      // If region is not proxied, try to create a proxy
      if (region == null) {
        try {
          region = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY)
              .create(regionName);
        } catch (Exception e) {
          result.put("success", false);
          result.put("error", "Region not found and could not create proxy: " + regionName);
          return result;
        }
      }

      List<Map<String, Object>> entries = region.entrySet().stream()
          .skip(offset != null ? offset : 0)
          .limit(limit != null ? limit : 100)
          .map(entry -> {
            Map<String, Object> entryData = new HashMap<>();
            entryData.put("key", entry.getKey());
            entryData.put("value", entry.getValue());
            entryData.put("valueType", entry.getValue() != null ? 
                entry.getValue().getClass().getName() : null);
            return entryData;
          })
          .collect(Collectors.toList());
      
      result.put("success", true);
      result.put("region", regionName);
      result.put("entries", entries);
      result.put("totalSize", region.size());
      result.put("returnedCount", entries.size());
      
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Failed to get data: " + e.getMessage());
    }
    
    return result;
  }

  /**
   * Put a key-value pair into a region.
   */
  public Map<String, Object> putData(String regionName, String key, Object value) {
    Map<String, Object> result = new HashMap<>();
    
    if (clientCache == null || clientCache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      Region<Object, Object> region = clientCache.getRegion(regionName);
      
      // If region is not proxied, try to create a proxy
      if (region == null) {
        try {
          region = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY)
              .create(regionName);
        } catch (Exception e) {
          result.put("success", false);
          result.put("error", "Region not found and could not create proxy: " + regionName);
          return result;
        }
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
   * Delete a key from a region.
   */
  public Map<String, Object> deleteData(String regionName, String key) {
    Map<String, Object> result = new HashMap<>();
    
    if (clientCache == null || clientCache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      Region<Object, Object> region = clientCache.getRegion(regionName);
      
      // If region is not proxied, try to create a proxy
      if (region == null) {
        try {
          region = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY)
              .create(regionName);
        } catch (Exception e) {
          result.put("success", false);
          result.put("error", "Region not found and could not create proxy: " + regionName);
          return result;
        }
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
}
