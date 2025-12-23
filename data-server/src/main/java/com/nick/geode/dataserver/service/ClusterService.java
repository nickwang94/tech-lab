package com.nick.geode.dataserver.service;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.distributed.DistributedMember;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for querying cluster status and information.
 */
@Service
public class ClusterService {

  /**
   * Get the cache instance directly from CacheFactory to avoid proxy issues.
   */
  private GemFireCache getCache() {
    try {
      GemFireCache cache = CacheFactory.getAnyInstance();
      if (cache != null && !cache.isClosed()) {
        return cache;
      }
    } catch (Exception e) {
      // Cache not available
    }
    return null;
  }

  /**
   * Get cluster status information.
   */
  public Map<String, Object> getClusterStatus() {
    Map<String, Object> status = new HashMap<>();
    
    GemFireCache cache = getCache();
    if (cache == null || cache.isClosed()) {
      status.put("status", "disconnected");
      return status;
    }

    status.put("status", "connected");
    status.put("cacheName", cache.getName());
    
    // Get all members (other members + current member)
    Set<DistributedMember> otherMembers = cache.getDistributedSystem().getAllOtherMembers();
    Set<DistributedMember> allMembers = new java.util.HashSet<>(otherMembers);
    DistributedMember currentMember = cache.getDistributedSystem().getDistributedMember();
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
    
    return status;
  }
}

