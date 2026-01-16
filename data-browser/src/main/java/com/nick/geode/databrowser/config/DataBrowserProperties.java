package com.nick.geode.databrowser.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "geode.browser")
public class DataBrowserProperties {
  
  private String locatorHost = "localhost";
  private int locatorPort = 10334;
  private String memberName = "data-browser";
  private String dataServerUrl = "http://localhost:8080";

  public String getLocatorHost() {
    return locatorHost;
  }

  public void setLocatorHost(String locatorHost) {
    this.locatorHost = locatorHost;
  }

  public int getLocatorPort() {
    return locatorPort;
  }

  public void setLocatorPort(int locatorPort) {
    this.locatorPort = locatorPort;
  }

  public String getMemberName() {
    return memberName;
  }

  public void setMemberName(String memberName) {
    this.memberName = memberName;
  }

  public String getDataServerUrl() {
    return dataServerUrl;
  }

  public void setDataServerUrl(String dataServerUrl) {
    this.dataServerUrl = dataServerUrl;
  }
}
