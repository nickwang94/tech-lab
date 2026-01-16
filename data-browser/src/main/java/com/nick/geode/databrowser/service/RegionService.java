package com.nick.geode.databrowser.service;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.beans.factory.annotation.Autowired;
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

  private final ClientCache clientCache;

  @Autowired
  public RegionService(ClientCache clientCache) {
    this.clientCache = clientCache;
  }

  /**
   * Get all region names in the cache.
   * For ClientCache, this will return regions that are already proxied.
   * To see server-side regions, we need to query them or use a different approach.
   */
  public List<String> getAllRegionNames() {
    if (clientCache == null || clientCache.isClosed()) {
      return List.of();
    }
    
    try {
      Set<Region<?, ?>> regions = clientCache.rootRegions();
      return regions.stream()
          .map(Region::getName)
          .collect(Collectors.toList());
    } catch (Exception e) {
      // If no regions are proxied, return empty list
      // In a real scenario, you might want to query the server for available regions
      return List.of();
    }
  }

  /**
   * Get region information.
   * For ClientCache, the region must be proxied first. If not found, we try to create a proxy.
   */
  public Map<String, Object> getRegionInfo(String regionName) {
    Map<String, Object> info = new HashMap<>();
    
    if (clientCache == null || clientCache.isClosed()) {
      info.put("error", "Cache is not available");
      return info;
    }

    try {
      Region<?, ?> region = clientCache.getRegion(regionName);
      
      // If region is not proxied, try to create a proxy
      if (region == null) {
        try {
          // Create a PROXY region to access server-side data
          region = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY)
              .create(regionName);
        } catch (Exception e) {
          info.put("error", "Region not found and could not create proxy: " + regionName + " - " + e.getMessage());
          return info;
        }
      }

      info.put("name", region.getName());
      info.put("fullPath", region.getFullPath());
      
      try {
        info.put("size", region.size());
      } catch (Exception e) {
        // Size might not be available for all region types
        info.put("size", "N/A");
        info.put("sizeError", e.getMessage());
      }
      
      info.put("attributes", region.getAttributes().toString());
      
      // Get more detailed attributes
      Map<String, Object> attributes = new HashMap<>();
      attributes.put("dataPolicy", region.getAttributes().getDataPolicy() != null ? 
          region.getAttributes().getDataPolicy().toString() : "N/A");
      attributes.put("scope", region.getAttributes().getScope() != null ? 
          region.getAttributes().getScope().toString() : "N/A");
      attributes.put("poolName", region.getAttributes().getPoolName());
      info.put("detailedAttributes", attributes);
      
    } catch (Exception e) {
      info.put("error", "Failed to get region info: " + e.getMessage());
    }
    
    return info;
  }

  /**
   * Get all regions with their basic information.
   * For ClientCache, this returns only proxied regions.
   */
  public List<Map<String, Object>> getAllRegionsInfo() {
    if (clientCache == null || clientCache.isClosed()) {
      return List.of();
    }
    
    try {
      Set<Region<?, ?>> regions = clientCache.rootRegions();
      return regions.stream()
          .map(region -> {
            Map<String, Object> info = new HashMap<>();
            info.put("name", region.getName());
            info.put("fullPath", region.getFullPath());
            try {
              info.put("size", region.size());
            } catch (Exception e) {
              info.put("size", "N/A");
            }
            return info;
          })
          .collect(Collectors.toList());
    } catch (Exception e) {
      return List.of();
    }
  }

  /**
   * Ensure a region is proxied in the client cache.
   * This is called after creating a region on the server to make it visible.
   */
  public Map<String, Object> ensureRegionProxied(String regionName) {
    Map<String, Object> result = new HashMap<>();
    
    if (clientCache == null || clientCache.isClosed()) {
      result.put("success", false);
      result.put("error", "Cache is not available");
      return result;
    }

    try {
      // Check if region already exists
      Region<?, ?> region = clientCache.getRegion(regionName);
      
      if (region != null) {
        result.put("success", true);
        result.put("message", "Region already proxied");
        return result;
      }

      // Create a PROXY region to access server-side data
      region = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY)
          .create(regionName);
      
      result.put("success", true);
      result.put("message", "Region proxy created successfully");
      result.put("regionName", region.getName());
      
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Failed to create region proxy: " + e.getMessage());
    }
    
    return result;
  }
}
