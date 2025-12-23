package com.nick.geode.dataserver.geode;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for running an embedded Apache Geode Data Server.
 */
@ConfigurationProperties(prefix = "geode.dataserver")
public class DataServerProperties {

  /**
   * Whether the embedded data server should be started.
   */
  private boolean enabled = true;

  /**
   * Geode member name.
   */
  private String memberName = "data-server";

  /**
   * Data server working directory (relative or absolute).
   */
  private String workingDir = "target/data-server";

  /**
   * Locator host to connect to.
   */
  private String locatorHost = "localhost";

  /**
   * Locator port to connect to.
   */
  private int locatorPort = 10334;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getMemberName() {
    return memberName;
  }

  public void setMemberName(String memberName) {
    this.memberName = memberName;
  }

  public String getWorkingDir() {
    return workingDir;
  }

  public void setWorkingDir(String workingDir) {
    this.workingDir = workingDir;
  }

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
}

