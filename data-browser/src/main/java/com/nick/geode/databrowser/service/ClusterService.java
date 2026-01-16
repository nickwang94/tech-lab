package com.nick.geode.databrowser.service;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.distributed.DistributedMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for querying cluster status and information.
 */
@Service
public class ClusterService {

  private final ClientCache clientCache;

  @Autowired
  public ClusterService(ClientCache clientCache) {
    this.clientCache = clientCache;
  }

  /**
   * Get cluster status information.
   */
  public Map<String, Object> getClusterStatus() {
    Map<String, Object> status = new HashMap<>();
    
    if (clientCache == null || clientCache.isClosed()) {
      status.put("status", "disconnected");
      return status;
    }

    try {
      status.put("status", "connected");
      status.put("cacheName", clientCache.getName());
      
      // For ClientCache, we can get distributed system info
      // Note: ClientCache may have limited access to member information
      try {
        Set<DistributedMember> otherMembers = clientCache.getDistributedSystem().getAllOtherMembers();
        Set<DistributedMember> allMembers = new java.util.HashSet<>(otherMembers);
        DistributedMember currentMember = clientCache.getDistributedSystem().getDistributedMember();
        if (currentMember != null) {
          allMembers.add(currentMember);
        }
        status.put("memberCount", allMembers.size());
        
        status.put("members", allMembers.stream()
            .map(member -> {
              Map<String, Object> memberInfo = new HashMap<>();
              memberInfo.put("id", member.getId());
              memberInfo.put("name", member.getName());
              memberInfo.put("host", member.getHost());
              memberInfo.put("groups", member.getGroups());
              return memberInfo;
            })
            .collect(Collectors.toList()));
      } catch (Exception e) {
        // If we can't get member info (common for client cache), just report connection status
        status.put("memberCount", 0);
        status.put("members", List.of());
        status.put("note", "Member information may be limited for client cache connections");
      }
    } catch (Exception e) {
      status.put("status", "error");
      status.put("error", e.getMessage());
    }
    
    return status;
  }
}
