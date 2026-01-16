package com.nick.geode.databrowser.controller;

import com.nick.geode.databrowser.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for data operations.
 */
@RestController
@RequestMapping("/api/data")
public class DataController {

  private final DataService dataService;

  @Autowired
  public DataController(DataService dataService) {
    this.dataService = dataService;
  }

  @GetMapping("/{regionName}/keys")
  public Map<String, Object> getAllKeys(@PathVariable String regionName) {
    return dataService.getAllKeys(regionName);
  }

  @GetMapping("/{regionName}")
  public Map<String, Object> getAllData(
      @PathVariable String regionName,
      @RequestParam(required = false) Integer limit,
      @RequestParam(required = false) Integer offset) {
    return dataService.getAllData(regionName, limit, offset);
  }

  @GetMapping("/{regionName}/{key}")
  public Map<String, Object> getData(
      @PathVariable String regionName,
      @PathVariable String key) {
    return dataService.getData(regionName, key);
  }

  @PostMapping("/{regionName}/{key}")
  public Map<String, Object> putData(
      @PathVariable String regionName,
      @PathVariable String key,
      @RequestBody Object value) {
    return dataService.putData(regionName, key, value);
  }

  @DeleteMapping("/{regionName}/{key}")
  public Map<String, Object> deleteData(
      @PathVariable String regionName,
      @PathVariable String key) {
    return dataService.deleteData(regionName, key);
  }
}
