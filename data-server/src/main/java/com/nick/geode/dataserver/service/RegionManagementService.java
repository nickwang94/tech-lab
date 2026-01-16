package com.nick.geode.dataserver.service;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionExistsException;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing Geode regions on the server side.
 */
@Service
public class RegionManagementService {

  /**
   * Get the cache instance directly from CacheFactory.
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
   * Create a new region on the server.
   */
  public Map<String, Object> createRegion(String regionName, String regionType) {
    Map<String, Object> result = new HashMap<>();
    
    Cache cache = getCache();
    if (cache == null || cache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      // Check if region already exists
      if (cache.getRegion(regionName) != null) {
        result.put("success", false);
        result.put("error", "Region already exists: " + regionName);
        return result;
      }

      // Parse region type (default to PARTITION)
      RegionShortcut shortcut = parseRegionType(regionType);
      
      RegionFactory<?, ?> regionFactory = cache.createRegionFactory(shortcut);
      Region<?, ?> region = regionFactory.create(regionName);
      
      result.put("success", true);
      result.put("message", "Region created successfully: " + regionName);
      result.put("regionName", region.getName());
      result.put("regionType", shortcut.toString());
      
    } catch (RegionExistsException e) {
      result.put("success", false);
      result.put("error", "Region already exists: " + regionName);
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Failed to create region: " + e.getMessage());
    }
    
    return result;
  }

  /**
   * Delete a region from the server.
   */
  public Map<String, Object> deleteRegion(String regionName) {
    Map<String, Object> result = new HashMap<>();
    
    Cache cache = getCache();
    if (cache == null || cache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      Region<?, ?> region = cache.getRegion(regionName);
      
      if (region == null) {
        result.put("success", false);
        result.put("error", "Region not found: " + regionName);
        return result;
      }

      region.destroyRegion();
      
      result.put("success", true);
      result.put("message", "Region deleted successfully: " + regionName);
      
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Failed to delete region: " + e.getMessage());
    }
    
    return result;
  }

  /**
   * Parse region type string to RegionShortcut enum.
   */
  private RegionShortcut parseRegionType(String regionType) {
    if (regionType == null || regionType.isEmpty()) {
      return RegionShortcut.PARTITION;
    }
    
    try {
      return RegionShortcut.valueOf(regionType.toUpperCase());
    } catch (IllegalArgumentException e) {
      // Default to PARTITION if invalid type
      return RegionShortcut.PARTITION;
    }
  }
}
