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

import com.fasterxml.jackson.databind.JsonNode;
import de.griefed.monitoring.ApplicationProperties;
import de.griefed.monitoring.utilities.MailNotification;
import de.griefed.monitoring.utilities.WebUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class responsible for collecting information from all components and building a JSON string with them.
 * @author Griefed
 */
@Service
public class InformationService {

    private static final Logger LOG = LogManager.getLogger(InformationService.class);

    private final ApplicationProperties APPLICATION_PROPERTIES;
    private final MailNotification MAIL_NOTIFICATION;
    private final WebUtilities WEB_UTILITIES;

    private String hostsInformation = "{\"hosts\": []}";

    /**
     * Constructor responsible for DI.
     * @author Griefed
     * @param injectedApplicationProperties Instance of {@link ApplicationProperties}.
     * @param injectedMailNotification Instance of {@link MailNotification}.
     * @param injectedWebUtilities Instance of {@link WebUtilities}.
     */
    @Autowired
    public InformationService(ApplicationProperties injectedApplicationProperties,
                              MailNotification injectedMailNotification,
                              WebUtilities injectedWebUtilities) {

        this.APPLICATION_PROPERTIES = injectedApplicationProperties;
        this.MAIL_NOTIFICATION = injectedMailNotification;
        this.WEB_UTILITIES = injectedWebUtilities;

        poll();
    }

    /**
     * Retrieve hosts information.
     * @author Griefed
     * @return String in JSON format. Returns information about the configured host(s).
     */
    public String retrieveHostsInformation() {
        return hostsInformation;
    }

    /**
     * Retrieve all information about the configured host(s) and stores it in memory for retrieval by {@link #retrieveHostsInformation()}.
     * @author Griefed
     */
    public void poll() {

        JsonNode hosts = null;
        String info;

        try {
            hosts = APPLICATION_PROPERTIES.getHosts();
        } catch (IOException ex) {
            LOG.error("Error acquiring hosts JSON.",ex);
        }

        // If host-configuration is default, do not retrieve anything.
        if (hosts == null || hosts.size() == 0) {

            LOG.warn("WARNING! Hosts are not configured! Not retrieving information.");

            info = "{\"status\": " + 1 + ",\"message\": \"Hosts are not configured! Not retrieving information.\"}";

        } else {

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("{\"hosts\": [");

            // Retrieve all information for all hosts if more than one is configured
            if (hosts.size() > 1) {

                LOG.debug("Thread count: " + APPLICATION_PROPERTIES.getThreadCount());

                List<CompletableFuture<String>> completableFuturesList = new ArrayList<>(1000);

                ForkJoinPool forkJoinPool = new ForkJoinPool(APPLICATION_PROPERTIES.getThreadCount());

                String firstName = hosts.get(0).get("name").asText();
                String firstAddress = hosts.get(0).get("address").asText();

                String lastName = hosts.get(hosts.size() - 1).get("name").asText();
                String lastAddress = hosts.get(hosts.size() - 1).get("address").asText();

                List<Integer> firstPorts = new ArrayList<>(100);
                List<Integer> lastPorts = new ArrayList<>(100);

                try {
                    String[] ports = hosts.get(0).get("ports").asText().split(",");
                    for (String port : ports) {
                        try {
                            firstPorts.add(Integer.parseInt(port));
                        } catch (NumberFormatException ex) {
                            LOG.error("Couldn't parse String to Integer: " + port);
                        }

                    }
                } catch (Exception ignored) {
                }

                try {
                    String[] ports = hosts.get(hosts.size() - 1).get("ports").asText().split(",");
                    for (String port : ports) {

                        try {
                            lastPorts.add(Integer.parseInt(port));
                        } catch (NumberFormatException ex) {
                            LOG.error("Couldn't parse String to Integer: " + port);
                        }

                    }
                } catch (Exception ignored) {
                }

                completableFuturesList.add(
                        CompletableFuture.supplyAsync(() -> getHostInformationAsJson(firstName, firstAddress, firstPorts), forkJoinPool)
                );

                completableFuturesList.add(
                        CompletableFuture.supplyAsync(() -> getHostInformationAsJson(lastName, lastAddress, lastPorts), forkJoinPool)
                );

                for (int i = 1; i < hosts.size() - 1; i++) {

                    String nameI = hosts.get(i).get("name").asText();
                    String addressI = hosts.get(i).get("address").asText();

                    List<Integer> portsI = new ArrayList<>(100);

                    try {
                        String[] ports = hosts.get(i).get("ports").asText().split(",");
                        for (String port : ports) {

                            try {
                                portsI.add(Integer.parseInt(port));
                            } catch (NumberFormatException ex) {
                                LOG.error("Couldn't parse String to Integer: " + port);
                            }

                        }
                    } catch (Exception ignored) {
                    }

                    completableFuturesList.add(
                            CompletableFuture.supplyAsync(() -> getHostInformationAsJson(nameI, addressI, portsI), forkJoinPool)
                    );
                }

                try {

                    stringBuilder.append(
                            completableFuturesList
                                    .stream()
                                    .map(
                                            CompletableFuture::join
                                    )
                                    .collect(
                                            Collectors.joining(",")
                                    )
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Retrieve information for host if only one is configured
            } else {

                List<Integer> oneHostPorts = new ArrayList<>(100);

                try {
                    String[] ports = hosts.get(0).get("ports").asText().split(",");
                    for (String port : ports) {

                        try {
                            oneHostPorts.add(Integer.parseInt(port));
                        } catch (NumberFormatException ex) {
                            LOG.error("Couldn't parse String to Integer: " + port);
                        }

                    }
                } catch (Exception ignored) {
                }

                stringBuilder.append(getHostInformationAsJson(hosts.get(0).get("name").asText(), hosts.get(0).get("address").asText(), oneHostPorts));

            }

            stringBuilder.append("]}");

            info = stringBuilder.toString();

        }

        this.hostsInformation = info;

        LOG.info("Retrieved information.");

    }

    /**
     * Acquire status information for a given host with name <code>name</code> and address <code>address</code>.
     * @author Griefed
     * @param name {@link String} The name of the host.
     * @param address {@link String} The address of the host to check.
     * @return {@link String} Name, address, IP, availability, status, code wrapped in JSON.
     */
    private String getHostInformationAsJson(String name, String address, List<Integer> ports) {

        String status;
        String ip;

        int code;

        boolean hostAvailable = false;

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
            hostAvailable = WEB_UTILITIES.ping(address, ip, ports);

        }

        if ((code != 200 && code != 301) || !status.matches("^(OK|REDIRECT)$")) {
            sendNotification(name + " (" + address + ") ",status);
        }

        return "{" +
                "\"name\":\"" + name + "\"," +
                "\"address\":\"" + address + "\"," +
                "\"ip\":\"" + ip + "\"," +
                "\"hostAvailable\":" + hostAvailable + "," +
                "\"status\":\"" + status + "\"," +
                "\"code\":" + code +
                "}";
    }

    /**
     * Sends a notification,
     * @author Griefed
     * @param host String. The host to mention in the notifications body.
     * @param status Integer. The statuscode of the host.
     */
    private void sendNotification(String host, String status) {
        try {
            MAIL_NOTIFICATION.sendMailNotification(
                    "Host down or unreachable",
                    "The host " + host + " is unreachable or down! Status: " + status
            );
        } catch (MessagingException ex) {

            LOG.error("Error sending notification for host " + host, ex);

        }

    }
}
