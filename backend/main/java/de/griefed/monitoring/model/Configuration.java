package de.griefed.monitoring.model;

import java.util.List;

public class Configuration {

  private Settings settings;
  private List<Host> hosts;

  public Settings getSettings() {
    return settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  public List<Host> getHosts() {
    return hosts;
  }

  public void setHosts(List<Host> hosts) {
    this.hosts = hosts;
  }

  @Override
  public String toString() {
    return "Configuration{" +
        "settings=" + settings +
        ", hosts=" + hosts +
        '}';
  }
}
