package de.griefed.monitoring.model.response;

import java.util.List;

public class Hosts {

  private List<Host> hostsOk;
  private List<Host> hostsDown;

  public List<Host> getHostsOk() {
    return hostsOk;
  }

  public void setHostsOk(List<Host> hostsOk) {
    this.hostsOk = hostsOk;
  }

  public List<Host> getHostsDown() {
    return hostsDown;
  }

  public void setHostsDown(List<Host> hostsDown) {
    this.hostsDown = hostsDown;
  }
}
