package com.nick.geode.locatorlearning.geode;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for running an embedded Apache Geode Locator.
 */
@ConfigurationProperties(prefix = "geode.locator")
public class LocatorProperties {

  /**
   * Whether the embedded locator should be started.
   */
  private boolean enabled = true;

  /**
   * Locator port.
   */
  private int port = 10334;

  /**
   * Geode member name.
   */
  private String memberName = "locator-learning";

  /**
   * Locator working directory (relative or absolute).
   */
  private String workingDir = "target/locator";

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
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
}
