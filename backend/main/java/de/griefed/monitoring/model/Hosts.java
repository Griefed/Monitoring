package de.griefed.monitoring.model;

import java.util.List;

public class Hosts {

  private List<Host> hosts;

  public Hosts(List<Host> hosts) {
    this.hosts = hosts;
  }

  public List<Host> getHosts() {
    return hosts;
  }

  public void setHosts(List<Host> hosts) {
    this.hosts = hosts;
  }
}
