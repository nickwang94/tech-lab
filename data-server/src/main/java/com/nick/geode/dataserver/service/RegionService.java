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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing Geode regions.
 */
@Service
public class RegionService {

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
   * Get all region names in the cache.
   */
  public List<String> getAllRegionNames() {
    Cache cache = getCache();
    if (cache == null || cache.isClosed()) {
      return List.of();
    }
    
    Set<Region<?, ?>> regions = cache.rootRegions();
    return regions.stream()
        .map(Region::getName)
        .collect(Collectors.toList());
  }

  /**
   * Get region information.
   */
  public Map<String, Object> getRegionInfo(String regionName) {
    Map<String, Object> info = new HashMap<>();
    
    Cache cache = getCache();
    if (cache == null || cache.isClosed()) {
      info.put("error", "Cache is not available");
      return info;
    }

    Region<?, ?> region = cache.getRegion(regionName);
    if (region == null) {
      info.put("error", "Region not found: " + regionName);
      return info;
    }

    info.put("name", region.getName());
    info.put("fullPath", region.getFullPath());
    info.put("size", region.size());
    info.put("attributes", region.getAttributes().toString());
    
    return info;
  }

  /**
   * Create a new region.
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
   * Delete a region.
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

