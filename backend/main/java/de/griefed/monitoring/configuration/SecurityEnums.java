package de.griefed.monitoring.configuration;

public enum SecurityEnums {
  /** Lock down the whole application behind security authentication. */
  ALL("ALL"),
  /** Secure changing of settings behind authentication. Leave the rest of Monitoring unsecured. */
  SETTINGS("SETTINGS"),
  /**
   * Completely disable security. Beware: This means any user who has access to your network can
   * potentially change every setting of Monitoring.
   */
  DEACTIVATE("DEACTIVATE");

  public final String setting;

  SecurityEnums(String setting) {
    this.setting = setting;
  }
}
