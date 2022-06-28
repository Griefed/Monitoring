/* MIT License
 *
 * Copyright (c) 2021 SUK-IT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.griefed.monitoring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.griefed.monitoring.configuration.SecurityEnums;
import de.griefed.monitoring.utilities.JsonUtilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Class responsible for handling our properties. Loaded at boot in {@link Main}.
 *
 * @author Griefed
 */
@Component
public class ApplicationProperties extends Properties {

  private static final Logger LOG = LogManager.getLogger(ApplicationProperties.class);
  /**
   * Security setting. Either {@link SecurityEnums#ALL},{@link SecurityEnums#SETTINGS}, or {@link
   * SecurityEnums#DEACTIVATE}. Changing this at runtime requires a restart in order for changes to
   * take effect.
   */
  public final SecurityEnums SECURITY_SETTING;
  private final File HOSTS_FILE = new File("hosts.json");
  private final JsonUtilities JSON_UTILITIES;
  private final String VERSION;
  /** Default ports with which to check for host availability if a regular ping failed. */
  private List<Integer> defaultPorts =
      new ArrayList<>(Arrays.asList(20, 21, 22, 80, 443, 8080, 8443));
  /** Whether default ports should be added to host-configured ports. */
  private boolean additivePorts = false;
  /** Whether notifications should be globally enabled or disabled. */
  private boolean notificationsEnabled = true;
  /** The amount of particles to display in the webfrontend. */
  private int particlesCount = 40;
  /** The rate in milliseconds at which the webfrontend should refresh. */
  private int pollingRate = 5000;
  /** Seconds to wait until a connection timeout is triggered for getting information from hosts. */
  private int timeoutConnect = 5;
  /** Seconds to wait until a read timeout is triggered for getting information from hosts. */
  private int timeoutAvailability = 5;
  /**
   * Number of threads used for updating host information. Changing this at runtime requires a
   * restart in order for changes to take effect.
   */
  private int threadCount = 40;

  /**
   * Constructor for our properties. Sets a couple of default values for use in Monitoring.
   *
   * @author Griefed
   * @param injectedJsonUtilities Instance of {@link JsonUtilities}.
   */
  @Autowired
  public ApplicationProperties(JsonUtilities injectedJsonUtilities) {
    this.JSON_UTILITIES = injectedJsonUtilities;

    if (!new File("application.properties").exists()) {
      try {
        FileUtils.copyInputStreamToFile(
            Objects.requireNonNull(
                Main.class.getResourceAsStream("/" + new File("application.properties").getName())),
            new File("application.properties"));
      } catch (IOException ex) {
        LOG.error("Error creating file: " + new File("application.properties"), ex);
      }
    }

    // Load the properties file from the classpath, providing default values.
    try (InputStream inputStream =
        new ClassPathResource("application.properties").getInputStream()) {
      load(inputStream);
    } catch (IOException ex) {
      LOG.error("Couldn't read properties file.", ex);
    }

    /*
     * Now load the properties file from the local filesystem. This overwrites previously loaded properties
     * but has the advantage of always providing default values if any property in the applications.properties
     * on the filesystem should be commented out.
     */
    try (InputStream inputStream = new FileInputStream("application.properties")) {
      load(inputStream);
    } catch (IOException ex) {
      LOG.error("Couldn't read properties file.", ex);
    }

    String version = ApplicationProperties.class.getPackage().getImplementationVersion();

    try {

      List<Integer> ports = new ArrayList<>(1000);

      if (getProperty("de.griefed.monitoring.host.ports").contains(",")) {

        List<String> portsAsStrings =
            new ArrayList<>(
                Arrays.asList(getProperty("de.griefed.monitoring.host.ports").split(",")));

        portsAsStrings.forEach(
            entry -> {
              try {
                ports.add(Integer.parseInt(entry));
              } catch (NumberFormatException ex) {
                LOG.error(entry + " is not a valid integer. Check your configuration.");
              }
            });

      } else {

        ports.add(Integer.parseInt(getProperty("de.griefed.monitoring.host.ports")));
      }

      this.defaultPorts = ports;

    } catch (NumberFormatException ex) {
      this.defaultPorts = new ArrayList<>(Arrays.asList(20, 21, 22, 80, 443, 8080, 8443));
    }

    try {
      this.additivePorts = Boolean.parseBoolean(getProperty("de.griefed.ports.additive"));
    } catch (Exception ex) {
      this.additivePorts = false;
    }

    try {
      this.timeoutConnect =
          Integer.parseInt(getProperty("de.griefed.monitoring.timeout.connect", "5"));
    } catch (NumberFormatException ex) {
      this.timeoutConnect = 5;
    }

    try {
      this.timeoutAvailability =
          Integer.parseInt(getProperty("de.griefed.monitoring.timeout.availability", "5"));
    } catch (NumberFormatException ex) {
      this.timeoutAvailability = 5;
    }

    try {
      this.threadCount = Integer.parseInt(getProperty("de.griefed.monitoring.thread.count", "40"));
    } catch (NumberFormatException ex) {
      this.threadCount = 40;
    }

    if (version != null) {
      this.VERSION = version;
    } else {
      this.VERSION = "dev";
    }

    try {
      this.pollingRate = Integer.parseInt(getProperty("de.griefed.monitoring.polling", "5000"));
    } catch (NumberFormatException ex) {
      LOG.error("polling configuration invalid. Not a parseable integer. Using default 5000.");
      this.pollingRate = 5000;
    }

    try {
      this.notificationsEnabled =
          Boolean.parseBoolean(getProperty("de.griefed.monitoring.notifications", "true"));
    } catch (Exception ex) {
      LOG.error("notifications configuration invalid. Using default true.");
      this.notificationsEnabled = true;
    }

    try {
      this.particlesCount =
          Integer.parseInt(getProperty("de.griefed.monitoring.particles.count", "40"));
    } catch (NumberFormatException ex) {
      LOG.error(
          "particles.count configuration invalid. Not a parseable integer. Using default 40.");
      this.particlesCount = 40;
    }

    switch (getProperty("de.griefed.monitoring.configuration.security")) {
      case "ALL":
        this.SECURITY_SETTING = SecurityEnums.ALL;
        break;
      case "SETTINGS":
        this.SECURITY_SETTING = SecurityEnums.SETTINGS;
        break;
      case "DEACTIVATE":
        this.SECURITY_SETTING = SecurityEnums.DEACTIVATE;
        break;
      default:
        LOG.error(
            "Security setting invalid. Defaulting to ALL. Setting must be either ALL, SETTINGS, or DEACTIVATE.");
        this.SECURITY_SETTING = SecurityEnums.ALL;
        break;
    }
  }

  /**
   * Getter for a list of hosts to gather information from.
   *
   * @author Griefed
   * @return List String. Each entry is a combination of ip and access token in the format <code>
   *     host_ip1,access_token1</code>.
   * @throws IOException when hosts can not be read from the file.
   */
  public JsonNode getHosts() throws IOException {
    return JSON_UTILITIES.getJson(HOSTS_FILE).get("hosts");
  }

  /**
   * Getter for a list of ports to scan in {@link de.griefed.monitoring.services.InformationService}
   * for host availability.
   *
   * @author Griefed
   * @return List int. Returns a list of ports which will be scanned to determine host availability.
   */
  public List<Integer> getPorts() {
    return defaultPorts;
  }
  /**
   * Setter for the list of ports with which to check for host availability in case a regular ping
   * fails.
   *
   * @author Griefed
   * @param ports {@link List} {@link Integer} List of ports.
   */
  protected void setDefaultPorts(List<Integer> ports) {
    this.defaultPorts = ports;
  }

  /**
   * Getter for whether default ports should be added to host-configured ports.
   *
   * @author Griefed
   * @return {@link Boolean} True if default ports should be added to host-configured ports.
   */
  public Boolean additivePorts() {
    return additivePorts;
  }
  /**
   * Setter for whether default ports should be added to host-configured ports.
   *
   * @author Griefed
   * @param additivePorts {@link Boolean} <code>true</code> to have default ports added to
   *     host-configured ports.
   */
  protected void setAdditivePorts(boolean additivePorts) {
    this.additivePorts = additivePorts;
  }

  /**
   * Getter for the number of seconds to wait until a connection timeout is triggered for getting
   * information from hosts.
   *
   * @author Griefed
   * @return Integer. Returns the number of seconds as an int.
   */
  public int getTimeoutConnect() {
    return timeoutConnect;
  }
  /**
   * Setter for the seconds to wait until a connection timeout is triggered for getting information
   * from hosts.
   *
   * @author Griefed
   * @param timeout {@link Integer} Timeout in seconds.
   */
  protected void setTimeoutConnect(int timeout) {
    this.timeoutConnect = timeout;
  }

  /**
   * Getter for the number of seconds to wait until a read timeout is triggered for getting
   * information from hosts.
   *
   * @author Griefed
   * @return Integer. Returns the number of seconds as an int.
   */
  public int getTimeoutAvailability() {
    return timeoutAvailability;
  }
  /**
   * Setter for the time to wait until a read timeout is triggered for getting information from
   * hsots.
   *
   * @author Griefed
   * @param timeout {@link Integer} Timeout in seconds.
   */
  protected void setTimeoutAvailability(int timeout) {
    this.timeoutAvailability = timeout;
  }

  /**
   * Getter for the number of threads used for updating host information.
   *
   * @author Griefed
   * @return {@link Integer} The number of threads used for updating host information.
   */
  public int getThreadCount() {
    return threadCount;
  }
  /**
   * Setter for the thread amount used to acquire host information.
   *
   * @author Griefed
   * @param count {@link Integer} Number of threads.
   */
  protected void setThreadCount(int count) {
    this.threadCount = count;
  }

  /**
   * The polling rate at which to update the webfrontend.
   *
   * @author Griefed
   * @return {@link Integer} The polling rate at which to update the webfrontend.
   */
  public int getPollingRate() {
    return pollingRate;
  }
  /**
   * Setter for the polling rate at which the webfrontend should refresh.
   *
   * @author Griefed
   * @param pollingRate {@link Integer} The polling rate at wich to update the webfrontend.
   */
  protected void setPollingRate(int pollingRate) {
    this.pollingRate = pollingRate;
  }

  /**
   * Getter for the version of this Monitoring instance.
   *
   * @author Griefed
   * @return {@link String} The version of this Monitoring instance.
   */
  public String getVersion() {
    return VERSION;
  }

  /**
   * Getter for the amount of particles to display in the frontend.
   *
   * @author Griefed
   * @return {@link Integer} The number of particles.
   */
  public int getParticlesCount() {
    return particlesCount;
  }
  /**
   * Setter for the amount of particles to display in the frontend.
   *
   * @author Griefed
   * @param particlesCount {@link Integer} The number of particles.
   */
  protected void setParticlesCount(int particlesCount) {
    this.particlesCount = particlesCount;
  }

  /**
   * Whether notifications are enabled on this instance of Monitoring.
   *
   * @author Griefed
   * @return {@link Boolean} Whether notifications are enabled. <code>true</code> if they are.
   */
  public boolean getNotificationsEnabled() {
    return notificationsEnabled;
  }
  /**
   * Setter for whether notifications are enabled.
   *
   * @author Griefed
   * @param notificationsEnabled {@link Boolean} <code>true</code> to activate.
   */
  protected void setNotificationsEnabled(boolean notificationsEnabled) {
    this.notificationsEnabled = notificationsEnabled;
  }

  /**
   * Getter for the security setting. Either {@link SecurityEnums#ALL},{@link
   * SecurityEnums#SETTINGS}, or {@link SecurityEnums#DEACTIVATE}.
   *
   * @author Griefed
   * @return {@link SecurityEnums} The security setting for this Monitoring instance.
   */
  public SecurityEnums getSecuritySetting() {
    return SECURITY_SETTING;
  }

  /**
   * Getter for the publicly available application settings.
   *
   * @return {@link JsonNode} Publicly available settings as a JsonNode.
   * @throws JsonProcessingException If the settings could not be parsed into JSON.
   */
  public JsonNode getPublicSettings() throws JsonProcessingException {
    return JSON_UTILITIES.getJson(
        "{"
            + "\"version\":\""
            + getVersion()
            + "\","
            + "\"pollingRate\":"
            + getPollingRate()
            + ","
            + "\"particlesCount\":"
            + getParticlesCount()
            + "}");
  }

  /**
   * Getter for the complete application configuration, including any configured hosts.
   *
   * @author Griefed
   * @return {@link JsonNode} Complete application configuration, including any configured hosts, as
   *     JSON.
   * @throws IOException if either the hosts.json file could not be read or the settings could not
   *     be parsed into JSON.
   */
  public JsonNode getAdminSettings() throws IOException {
    return JSON_UTILITIES.getJson(
        "{"
            + "\"hosts\":"
            + getHosts().toString()
            + ","
            + "\"settings\": {"
            + "\"defaultPorts\":"
            + getPorts()
            + ","
            + "\"additivePorts\":"
            + additivePorts()
            + ","
            + "\"notificationsEnabled\":"
            + getNotificationsEnabled()
            + ","
            + "\"particlesCount\":"
            + getParticlesCount()
            + ","
            + "\"pollingRate\":"
            + getPollingRate()
            + ","
            + "\"timeoutConnect\":"
            + getTimeoutConnect()
            + ","
            + "\"timeoutAvailability\":"
            + getTimeoutAvailability()
            + ","
            + "\"threadCount\":"
            + getThreadCount()
            + ","
            + "\"securitySetting\":"
            + "\""
            + getSecuritySetting()
            + "\""
            + "}"
            + "}");
  }
}
