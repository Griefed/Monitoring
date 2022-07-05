package de.griefed.monitoring.model.response;

import java.util.List;

public class Host {

  private String name;
  private String address;
  private String expectedIp;
  private List<Integer> ports;
  private String status;
  private String ip;
  private int code;

  public Host() {}

  public Host(String name, String address, String expectedIp, List<Integer> ports, String status, String ip, int code) {
    this.name = name;
    this.address = address;
    this.expectedIp = expectedIp;
    this.ports = ports;
    this.status = status;
    this.ip = ip;
    this.code = code;
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

  public String getExpectedIp() {
    return expectedIp;
  }

  public void setExpectedIp(String expectedIp) {
    this.expectedIp = expectedIp;
  }

  public List<Integer> getPorts() {
    return ports;
  }

  public void setPorts(List<Integer> ports) {
    this.ports = ports;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }
}
