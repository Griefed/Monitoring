package de.griefed.monitoring.model;

public class Host {

  private String name;
  private String address;
  private String ports;
  private String expectedIp;
  private boolean notificationsDisabled;

  public Host() {}

  public Host(String name) {
    this.name = name;
  }

  public Host(String name, String address) {
    this.name = name;
    this.address = address;
  }

  public Host(String name, String address, String ports) {
    this.name = name;
    this.address = address;
    this.ports = ports;
  }

  public Host(String name, String address, String ports, String expectedIp) {
    this.name = name;
    this.address = address;
    this.ports = ports;
    this.expectedIp = expectedIp;
  }

  public Host(
      String name, String address, String ports, String expectedIp, boolean notificationsDisabled) {
    this.name = name;
    this.address = address;
    this.ports = ports;
    this.expectedIp = expectedIp;
    this.notificationsDisabled = notificationsDisabled;
  }

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

  @Override
  public String toString() {
    return "Host{" +
            "name='" + name + '\'' +
            ", address='" + address + '\'' +
            ", ports='" + ports + '\'' +
            ", expectedIp='" + expectedIp + '\'' +
            ", notificationsDisabled=" + notificationsDisabled +
            '}';
  }
}
