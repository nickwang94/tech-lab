package com.nick.geode.databrowser.service;

import com.nick.geode.databrowser.config.DataBrowserProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Client service for calling data-server management API.
 */
@Service
public class RegionManagementClientService {

  private final RestTemplate restTemplate;
  private final DataBrowserProperties properties;

  @Autowired
  public RegionManagementClientService(DataBrowserProperties properties) {
    this.restTemplate = new RestTemplate();
    this.properties = properties;
  }

  /**
   * Create a region on the data server.
   */
  public Map<String, Object> createRegion(String regionName, String regionType) {
    Map<String, Object> result = new HashMap<>();
    
    try {
      String url = properties.getDataServerUrl() + "/management/regions/" + regionName;
      if (regionType != null && !regionType.isEmpty()) {
        url += "?type=" + regionType;
      }
      
      ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
      
      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        return response.getBody();
      } else {
        result.put("success", false);
        result.put("error", "Failed to create region: " + response.getStatusCode());
      }
    } catch (RestClientException e) {
      result.put("success", false);
      result.put("error", "Failed to connect to data server: " + e.getMessage());
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Unexpected error: " + e.getMessage());
    }
    
    return result;
  }

  /**
   * Delete a region from the data server.
   */
  public Map<String, Object> deleteRegion(String regionName) {
    Map<String, Object> result = new HashMap<>();
    
    try {
      String url = properties.getDataServerUrl() + "/management/regions/" + regionName;
      ResponseEntity<Map> response = restTemplate.exchange(url, 
          org.springframework.http.HttpMethod.DELETE, null, Map.class);
      
      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        return response.getBody();
      } else {
        result.put("success", false);
        result.put("error", "Failed to delete region: " + response.getStatusCode());
      }
    } catch (RestClientException e) {
      result.put("success", false);
      result.put("error", "Failed to connect to data server: " + e.getMessage());
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "Unexpected error: " + e.getMessage());
    }
    
    return result;
  }
}
