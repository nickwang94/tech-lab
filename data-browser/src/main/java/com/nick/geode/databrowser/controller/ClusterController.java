package com.nick.geode.databrowser.controller;

import com.nick.geode.databrowser.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for cluster status and information.
 */
@RestController
@RequestMapping("/api/cluster")
public class ClusterController {

  private final ClusterService clusterService;

  @Autowired
  public ClusterController(ClusterService clusterService) {
    this.clusterService = clusterService;
  }

  @GetMapping("/status")
  public Map<String, Object> getClusterStatus() {
    return clusterService.getClusterStatus();
  }
}
