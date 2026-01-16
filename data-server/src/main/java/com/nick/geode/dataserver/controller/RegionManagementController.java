package com.nick.geode.dataserver.controller;

import com.nick.geode.dataserver.service.RegionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for region management operations on the server side.
 * This is a lightweight management API for creating and deleting regions.
 */
@RestController
@RequestMapping("/management/regions")
public class RegionManagementController {

  private final RegionManagementService regionManagementService;

  @Autowired
  public RegionManagementController(RegionManagementService regionManagementService) {
    this.regionManagementService = regionManagementService;
  }

  /**
   * Create a new region.
   * POST /management/regions/{regionName}?type=PARTITION
   */
  @PostMapping("/{regionName}")
  public ResponseEntity<Map<String, Object>> createRegion(
      @PathVariable String regionName,
      @RequestParam(required = false, defaultValue = "PARTITION") String type) {
    Map<String, Object> result = regionManagementService.createRegion(regionName, type);
    boolean success = (Boolean) result.getOrDefault("success", false);
    return ResponseEntity.status(success ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
        .body(result);
  }

  /**
   * Delete a region.
   * DELETE /management/regions/{regionName}
   */
  @DeleteMapping("/{regionName}")
  public ResponseEntity<Map<String, Object>> deleteRegion(@PathVariable String regionName) {
    Map<String, Object> result = regionManagementService.deleteRegion(regionName);
    boolean success = (Boolean) result.getOrDefault("success", false);
    return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.NOT_FOUND)
        .body(result);
  }
}
