package de.griefed.monitoring.model.request;

public class Host {

  private String name;
  private String address;
  private String ports;
  private String expectedIp;
  private boolean notificationsDisabled;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPorts() {
    return ports;
  }

  public void setPorts(String ports) {
    this.ports = ports;
  }

  public String getExpectedIp() {
    return expectedIp;
  }

  public void setExpectedIp(String expectedIp) {
    this.expectedIp = expectedIp;
  }

  public boolean isNotificationsDisabled() {
    return notificationsDisabled;
  }

  public void setNotificationsDisabled(boolean notificationsDisabled) {
    this.notificationsDisabled = notificationsDisabled;
  }
}
