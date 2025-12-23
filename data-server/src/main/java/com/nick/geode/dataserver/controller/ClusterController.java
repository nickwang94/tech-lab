package com.nick.geode.dataserver.controller;

import com.nick.geode.dataserver.service.ClusterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for cluster status operations.
 */
@RestController
@RequestMapping("/api/cluster")
public class ClusterController {

  private final ClusterService clusterService;

  public ClusterController(ClusterService clusterService) {
    this.clusterService = clusterService;
  }

  /**
   * Get cluster status.
   * GET /api/cluster/status
   */
  @GetMapping("/status")
  public Map<String, Object> getClusterStatus() {
    return clusterService.getClusterStatus();
  }
}

