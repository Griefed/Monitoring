package de.griefed.monitoring.model;

import java.util.List;

public class Configuration {

  private Settings settings;
  private List<Host> hosts;

  public Configuration(Settings settings, List<Host> hosts) {
    this.settings = settings;
    this.hosts = hosts;
  }

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
    StringBuilder builder = new StringBuilder();
    for (Host host : hosts) {
      builder.append(host.toString());
    }

    return "Configuration{" +
            "settings=" + settings.toString() +
            ", hosts=" + builder.toString() +
            '}';
  }
}
