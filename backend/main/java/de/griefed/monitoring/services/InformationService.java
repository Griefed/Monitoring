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
package de.griefed.monitoring.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.griefed.monitoring.ApplicationProperties;
import de.griefed.monitoring.model.response.Host;
import de.griefed.monitoring.model.response.Hosts;
import de.griefed.monitoring.utilities.JsonUtilities;
import de.griefed.monitoring.utilities.MailNotification;
import de.griefed.monitoring.utilities.WebUtilities;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class responsible for collecting information from all components and building a JSON string with
 * them.
 *
 * @author Griefed
 */
@Service
public class InformationService {

  private static final Logger LOG = LogManager.getLogger(InformationService.class);

  private final ApplicationProperties APPLICATION_PROPERTIES;
  private final MailNotification MAIL_NOTIFICATION;
  private final WebUtilities WEB_UTILITIES;
  private final JsonUtilities JSON_UTILITIES;
  private final ForkJoinPool FORKJOINPOOL;
  private JsonNode hostsInformation = null;
  private Hosts responseHosts = new Hosts();

  /**
   * Constructor responsible for DI.
   *
   * @author Griefed
   * @param injectedApplicationProperties Instance of {@link ApplicationProperties}.
   * @param injectedMailNotification Instance of {@link MailNotification}.
   * @param injectedWebUtilities Instance of {@link WebUtilities}.
   */
  @Autowired
  public InformationService(
      ApplicationProperties injectedApplicationProperties,
      MailNotification injectedMailNotification,
      WebUtilities injectedWebUtilities,
      JsonUtilities injectedJsonUtilities) {

    this.APPLICATION_PROPERTIES = injectedApplicationProperties;
    this.MAIL_NOTIFICATION = injectedMailNotification;
    this.WEB_UTILITIES = injectedWebUtilities;
    this.JSON_UTILITIES = injectedJsonUtilities;

    FORKJOINPOOL = new ForkJoinPool(APPLICATION_PROPERTIES.getThreadCount());

    Executors.newSingleThreadExecutor()
        .execute(
            () -> {
              try {
                poll();
              } catch (JsonProcessingException ex) {
                LOG.error("Error acquiring and processing host information.", ex);
              }
            });
  }

  /**
   * Retrieve hosts information.
   *
   * @author Griefed
   * @return {@link JsonNode} containing all information about the configured hosts.
   */
  public JsonNode retrieveHostsInformation() {
    return hostsInformation;
  }

  /**
   * Retrieve all information about the configured host(s) and stores it in memory for retrieval by
   * {@link #retrieveHostsInformation()}.
   *
   * @author Griefed
   */
  public void poll() throws JsonProcessingException {

    JsonNode hosts = null;
    String info = "{" + "\"hostsOk\": []," + "\"hostsDown\": []" + "}";

    try {
      hosts = APPLICATION_PROPERTIES.getHosts();
    } catch (IOException ex) {
      LOG.error("Error acquiring hosts JSON.", ex);
    }

    // If host-configuration is default, do not retrieve anything.
    if (hosts == null || hosts.size() == 0) {

      LOG.warn("WARNING! Host are not configured! Not retrieving information.");

    } else {

      StringBuilder stringBuilder = new StringBuilder();

      stringBuilder.append("{\"hosts\": [");

      // Retrieve all information for all hosts if more than one is configured
      if (hosts.size() > 1) {

        LOG.debug("Thread count: " + APPLICATION_PROPERTIES.getThreadCount());

        List<CompletableFuture<String>> completableFuturesList = new ArrayList<>(1000);

        for (JsonNode host : hosts) {

          completableFuturesList.add(
              CompletableFuture.supplyAsync(() -> getHostInformationAsJson(host), FORKJOINPOOL));
        }

        try {

          stringBuilder.append(
              completableFuturesList.stream()
                  .map(CompletableFuture::join)
                  .collect(Collectors.joining(",")));
        } catch (Exception ex) {
          LOG.error("Error joining host information.", ex);
        }

        List<CompletableFuture<Host>> hostsFutures = new ArrayList<>(1000);

        for (JsonNode host : hosts) {

          hostsFutures.add(CompletableFuture.supplyAsync(() -> checkNGetHost(host), FORKJOINPOOL));
        }

        CompletableFuture<List<Host>> allDone = allOf(hostsFutures);
        try {
          allDone.get();
        } catch (ExecutionException e) {
          throw new RuntimeException(e);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        List<Host> ok = new ArrayList<>(100);
        List<Host> down = new ArrayList<>(100);

        hostsFutures.forEach(
            future -> {
              try {
                if (future.get().getStatus().equals("OK")) {
                  ok.add(future.get());
                } else {
                  down.add(future.get());
                }
              } catch (ExecutionException e) {
                throw new RuntimeException(e);
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }
            });

        responseHosts.setHostsOk(ok);
        responseHosts.setHostsDown(down);

        // Retrieve information for host if only one is configured
      } else {

        stringBuilder.append(getHostInformationAsJson(hosts.get(0)));
      }

      stringBuilder.append("]}");

      JsonNode infoNode = JSON_UTILITIES.getJson(stringBuilder.toString());

      if (infoNode.get("hosts").size() > 0) {

        List<String> ok = new ArrayList<>(100);
        List<String> down = new ArrayList<>(100);

        for (JsonNode host : infoNode.get("hosts")) {

          if (host.get("status").asText().equals("OK")) {
            ok.add(host.toPrettyString());
          } else {
            down.add(host.toPrettyString());
          }
        }

        info = "{" + "\"hostsOk\":" + ok + "," + "\"hostsDown\":" + down + "}";
      }
    }

    this.hostsInformation = JSON_UTILITIES.getJson(info);

    LOG.info("Retrieved information.");
  }

  /**
   * Acquire status information for a given host with name <code>name</code> and address <code>
   * address</code>.
   *
   * @author Griefed
   * @param host {@link JsonNode} JsonNode containing information about the host used to acquire
   *     more information.
   * @return {@link String} Name, address, IP, availability, status, code wrapped in JSON.
   */
  private String getHostInformationAsJson(JsonNode host) {

    String name = host.get("name").asText();
    String address = host.get("address").asText();
    String expectedIp = null;

    List<Integer> ports = new ArrayList<>(100);

    try {
      for (String port : host.get("ports").asText().split(",")) {
        try {
          ports.add(Integer.parseInt(port));
        } catch (NumberFormatException ex) {
          LOG.error("Couldn't parse String to Integer: " + port);
        }
      }
    } catch (Exception ignored) {
    }

    String status;
    String ip;

    int code;

    boolean hostAvailable;
    boolean hostNotificationsDisabled = false;

    if (address.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {

      ip = address;
      code = 418;
      hostAvailable = WEB_UTILITIES.ping(ip, ports);

      if (hostAvailable) {
        status = "OK";
        code = 200;
      } else {
        status = "DOWN";
      }

    } else {

      ip = WEB_UTILITIES.getIpOfHost(address);
      code = WEB_UTILITIES.getHostCode(address);
      status = WEB_UTILITIES.getHostStatus(address);

      try {
        expectedIp = host.get("expectedIp").asText();
        if (!expectedIp.equals(ip)) {
          status = "DNS MISMATCH";
          code = 418;
        }
      } catch (Exception ignored) {

      }

      if (expectedIp != null) {
        hostAvailable = WEB_UTILITIES.ping(address, expectedIp, ports);
      } else {
        hostAvailable = WEB_UTILITIES.ping(address, ip, ports);
      }
    }

    if (!hostAvailable && expectedIp != null) {
      status = "OFFLINE";
    } else if (!hostAvailable && ip != null) {
      status = "OFFLINE";
    }

    try {
      hostNotificationsDisabled = host.get("notificationsDisabled").asBoolean();
    } catch (Exception ignored) {

    }

    if (APPLICATION_PROPERTIES.getNotificationsEnabled() && !hostNotificationsDisabled) {

      if (code != 200 && code != 301 && !status.matches("^(OK|REDIRECT)$")) {
        sendNotification(name + " (" + address + ") ", status);
      }
    }

    return "{"
        + "\"name\":\""
        + name
        + "\","
        + "\"address\":\""
        + address
        + "\","
        + "\"ip\":\""
        + ip
        + "\","
        + "\"expectedIp\":\""
        + expectedIp
        + "\","
        + "\"hostAvailable\":"
        + hostAvailable
        + ","
        + "\"status\":\""
        + status
        + "\","
        + "\"code\":"
        + code
        + "}";
  }

  private Host checkNGetHost(JsonNode host) {

    Host responseHost = new Host();
    responseHost.setName(host.get("name").asText());
    responseHost.setAddress(host.get("address").asText());
    responseHost.setExpectedIp(null);

    List<Integer> ports = new ArrayList<>(100);

    try {
      for (String port : host.get("ports").asText().split(",")) {
        try {
          ports.add(Integer.parseInt(port));
        } catch (NumberFormatException ex) {
          LOG.error("Couldn't parse String to Integer: " + port);
        }
      }
    } catch (Exception ignored) {
    }
    responseHost.setPorts(ports);

    boolean hostAvailable;

    if (responseHost.getAddress().matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {

      responseHost.setIp(responseHost.getAddress());
      responseHost.setCode(418);
      hostAvailable = WEB_UTILITIES.ping(responseHost.getIp(), ports);

      if (hostAvailable) {
        responseHost.setStatus("OK");
        responseHost.setCode(200);
      } else {
        responseHost.setStatus("DOWN");
      }

    } else {

      responseHost.setIp(WEB_UTILITIES.getIpOfHost(responseHost.getAddress()));
      responseHost.setCode(WEB_UTILITIES.getHostCode(responseHost.getAddress()));
      responseHost.setStatus(WEB_UTILITIES.getHostStatus(responseHost.getAddress()));

      try {
        responseHost.setExpectedIp(host.get("expectedIp").asText());
        if (!responseHost.getExpectedIp().equals(responseHost.getIp())) {
          responseHost.setStatus("DNS MISMATCH");
          responseHost.setCode(418);
        }
      } catch (Exception ignored) {

      }

      if (responseHost.getExpectedIp() != null) {
        hostAvailable =
            WEB_UTILITIES.ping(responseHost.getAddress(), responseHost.getExpectedIp(), ports);
      } else {
        hostAvailable =
            WEB_UTILITIES.ping(
                responseHost.getAddress(), responseHost.getIp(), responseHost.getPorts());
      }
    }

    if (!hostAvailable && responseHost.getExpectedIp() != null) {
      responseHost.setStatus("OFFLINE");
    } else if (!hostAvailable && responseHost.getIp() != null) {
      responseHost.setStatus("OFFLINE");
    }

    try {
      if (APPLICATION_PROPERTIES.getNotificationsEnabled()
          && !host.get("notificationsDisabled").asBoolean()) {

        if (responseHost.getCode() != 200
            && responseHost.getCode() != 301
            && !responseHost.getStatus().matches("^(OK|REDIRECT)$")) {
          sendNotification(
              responseHost.getName() + " (" + responseHost.getAddress() + ") ",
              responseHost.getStatus());
        }
      }
    } catch (Exception ignored) {

    }

    return responseHost;
  }

  public <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futuresList) {
    return CompletableFuture.allOf(futuresList.toArray(new CompletableFuture<?>[0]))
        .thenApply(
            v -> futuresList.stream().map(CompletableFuture::join).collect(Collectors.<T>toList()));
  }

  /**
   * Sends a notification,
   *
   * @author Griefed
   * @param host String. The host to mention in the notifications body.
   * @param status Integer. The statuscode of the host.
   */
  private void sendNotification(String host, String status) {
    try {
      MAIL_NOTIFICATION.sendMailNotification(
          "Host down or unreachable",
          "The host " + host + " is unreachable or down! Status: " + status);
    } catch (MessagingException ex) {

      LOG.error("Error sending notification for host " + host, ex);
    }
  }
}
