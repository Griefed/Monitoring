package de.griefed.monitoring.model;

import de.griefed.monitoring.configuration.SecurityEnums;

public class Settings {

  private String defaultPorts;
  private boolean additivePorts;
  private boolean notificationsEnabled;
  private int particlesCount;
  private int pollingRate;
  private int timeoutConnect;
  private int timeoutAvailability;
  private int threadCount;
  private SecurityEnums securitySetting;

  public String getDefaultPorts() {
    return defaultPorts;
  }

  public void setDefaultPorts(String defaultPorts) {
    this.defaultPorts = defaultPorts;
  }

  public boolean isAdditivePorts() {
    return additivePorts;
  }

  public void setAdditivePorts(boolean additivePorts) {
    this.additivePorts = additivePorts;
  }

  public boolean isNotificationsEnabled() {
    return notificationsEnabled;
  }

  public void setNotificationsEnabled(boolean notificationsEnabled) {
    this.notificationsEnabled = notificationsEnabled;
  }

  public int getParticlesCount() {
    return particlesCount;
  }

  public void setParticlesCount(int particlesCount) {
    this.particlesCount = particlesCount;
  }

  public int getPollingRate() {
    return pollingRate;
  }

  public void setPollingRate(int pollingRate) {
    this.pollingRate = pollingRate;
  }

  public int getTimeoutConnect() {
    return timeoutConnect;
  }

  public void setTimeoutConnect(int timeoutConnect) {
    this.timeoutConnect = timeoutConnect;
  }

  public int getTimeoutAvailability() {
    return timeoutAvailability;
  }

  public void setTimeoutAvailability(int timeoutAvailability) {
    this.timeoutAvailability = timeoutAvailability;
  }

  public int getThreadCount() {
    return threadCount;
  }

  public void setThreadCount(int threadCount) {
    this.threadCount = threadCount;
  }

  public SecurityEnums getSecuritySetting() {
    return securitySetting;
  }

  public void setSecuritySetting(SecurityEnums securitySetting) {
    this.securitySetting = securitySetting;
  }
}
