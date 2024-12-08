package com.nick.gemfire.geode.controller;

import com.nick.gemfire.geode.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping("/region/query")
    public String queryRegion(@RequestParam String key) {
        return regionService.getRegionValue(key);
    }

    @GetMapping("/region/put")
    public String putRegion(@RequestParam String key, @RequestParam String value) {
        return regionService.putRegionEntity(key, value);
    }
}
