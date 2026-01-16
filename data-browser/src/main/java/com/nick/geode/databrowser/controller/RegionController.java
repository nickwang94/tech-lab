package com.nick.geode.databrowser.controller;

import com.nick.geode.databrowser.service.RegionManagementClientService;
import com.nick.geode.databrowser.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for region operations.
 */
@RestController
@RequestMapping("/api/regions")
public class RegionController {

  private final RegionService regionService;
  private final RegionManagementClientService regionManagementClientService;

  @Autowired
  public RegionController(RegionService regionService, 
                          RegionManagementClientService regionManagementClientService) {
    this.regionService = regionService;
    this.regionManagementClientService = regionManagementClientService;
  }

  @GetMapping
  public List<String> getAllRegionNames() {
    return regionService.getAllRegionNames();
  }

  @GetMapping("/info")
  public List<Map<String, Object>> getAllRegionsInfo() {
    return regionService.getAllRegionsInfo();
  }

  @GetMapping("/{regionName}")
  public Map<String, Object> getRegionInfo(@PathVariable String regionName) {
    return regionService.getRegionInfo(regionName);
  }

  /**
   * Create a new region on the data server.
   * POST /api/regions/{regionName}?type=PARTITION
   */
  @PostMapping("/{regionName}")
  public ResponseEntity<Map<String, Object>> createRegion(
      @PathVariable String regionName,
      @RequestParam(required = false, defaultValue = "PARTITION") String type) {
    Map<String, Object> result = regionManagementClientService.createRegion(regionName, type);
    boolean success = (Boolean) result.getOrDefault("success", false);
    
    // If region was created successfully, ensure it's proxied in the client cache
    if (success) {
      try {
        // Wait a bit for the region to be available on the server
        Thread.sleep(500);
        Map<String, Object> proxyResult = regionService.ensureRegionProxied(regionName);
        if (!(Boolean) proxyResult.getOrDefault("success", false)) {
          // Log but don't fail the request - region was created on server
          result.put("proxyWarning", "Region created but proxy setup had issues: " + 
              proxyResult.getOrDefault("error", "Unknown"));
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        // Log but don't fail the request
        result.put("proxyWarning", "Failed to create proxy: " + e.getMessage());
      }
    }
    
    return ResponseEntity.status(success ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
        .body(result);
  }

  /**
   * Delete a region from the data server.
   * DELETE /api/regions/{regionName}
   */
  @DeleteMapping("/{regionName}")
  public ResponseEntity<Map<String, Object>> deleteRegion(@PathVariable String regionName) {
    Map<String, Object> result = regionManagementClientService.deleteRegion(regionName);
    boolean success = (Boolean) result.getOrDefault("success", false);
    return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.NOT_FOUND)
        .body(result);
  }
}
