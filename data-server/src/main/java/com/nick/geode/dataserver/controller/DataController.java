package com.nick.geode.dataserver.controller;

import com.nick.geode.dataserver.service.DataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for data operations (put, get, delete).
 */
@RestController
@RequestMapping("/api/data")
public class DataController {

  private final DataService dataService;

  public DataController(DataService dataService) {
    this.dataService = dataService;
  }

  /**
   * Put data into a region.
   * PUT /api/data/{regionName}/{key}
   * Body: { "value": "your value" }
   */
  @PutMapping("/{regionName}/{key}")
  public ResponseEntity<Map<String, Object>> putData(
      @PathVariable String regionName,
      @PathVariable String key,
      @RequestBody Map<String, Object> request) {
    Object value = request.get("value");
    Map<String, Object> result = dataService.putData(regionName, key, value);
    boolean success = (Boolean) result.getOrDefault("success", false);
    return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
        .body(result);
  }

  /**
   * Get data from a region by key.
   * GET /api/data/{regionName}/{key}
   */
  @GetMapping("/{regionName}/{key}")
  public ResponseEntity<Map<String, Object>> getData(
      @PathVariable String regionName,
      @PathVariable String key) {
    Map<String, Object> result = dataService.getData(regionName, key);
    boolean success = (Boolean) result.getOrDefault("success", false);
    boolean exists = (Boolean) result.getOrDefault("exists", false);
    
    if (!success) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
    if (!exists) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }
    
    return ResponseEntity.ok(result);
  }

  /**
   * Delete data from a region by key.
   * DELETE /api/data/{regionName}/{key}
   */
  @DeleteMapping("/{regionName}/{key}")
  public ResponseEntity<Map<String, Object>> deleteData(
      @PathVariable String regionName,
      @PathVariable String key) {
    Map<String, Object> result = dataService.deleteData(regionName, key);
    boolean success = (Boolean) result.getOrDefault("success", false);
    return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
        .body(result);
  }

  /**
   * Get all keys in a region.
   * GET /api/data/{regionName}
   */
  @GetMapping("/{regionName}")
  public ResponseEntity<Map<String, Object>> getAllKeys(@PathVariable String regionName) {
    Map<String, Object> result = dataService.getAllKeys(regionName);
    boolean success = (Boolean) result.getOrDefault("success", false);
    return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
        .body(result);
  }
}

