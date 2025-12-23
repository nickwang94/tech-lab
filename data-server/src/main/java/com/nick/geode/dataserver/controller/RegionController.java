package com.nick.geode.dataserver.controller;

import com.nick.geode.dataserver.service.RegionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for region management operations.
 */
@RestController
@RequestMapping("/api/regions")
public class RegionController {

  private final RegionService regionService;

  public RegionController(RegionService regionService) {
    this.regionService = regionService;
  }

  /**
   * Get all region names.
   * GET /api/regions
   */
  @GetMapping
  public List<String> getAllRegions() {
    return regionService.getAllRegionNames();
  }

  /**
   * Get region information.
   * GET /api/regions/{regionName}
   */
  @GetMapping("/{regionName}")
  public Map<String, Object> getRegionInfo(@PathVariable String regionName) {
    return regionService.getRegionInfo(regionName);
  }

  /**
   * Create a new region.
   * POST /api/regions/{regionName}?type=PARTITION
   */
  @PostMapping("/{regionName}")
  public ResponseEntity<Map<String, Object>> createRegion(
      @PathVariable String regionName,
      @RequestParam(required = false, defaultValue = "PARTITION") String type) {
    Map<String, Object> result = regionService.createRegion(regionName, type);
    boolean success = (Boolean) result.getOrDefault("success", false);
    return ResponseEntity.status(success ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
        .body(result);
  }

  /**
   * Delete a region.
   * DELETE /api/regions/{regionName}
   */
  @DeleteMapping("/{regionName}")
  public ResponseEntity<Map<String, Object>> deleteRegion(@PathVariable String regionName) {
    Map<String, Object> result = regionService.deleteRegion(regionName);
    boolean success = (Boolean) result.getOrDefault("success", false);
    return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.NOT_FOUND)
        .body(result);
  }
}

