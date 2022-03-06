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
package de.griefed.monitoring.utilities;

import de.griefed.monitoring.ApplicationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.*;
import java.util.List;

@Component
public class WebUtilities {

    private static final Logger LOG = LogManager.getLogger(WebUtilities.class);

    private final ApplicationProperties APPLICATION_PROPERTIES;

    @Autowired
    public WebUtilities(ApplicationProperties injectedApplicationProperties) {
        this.APPLICATION_PROPERTIES = injectedApplicationProperties;
    }

    /**
     * Acquire the response from a given URL.
     * @author Griefed
     * @param hostUrl String. The URL to get the response from.
     * @return Integer. The status for the requested host.
     * @throws IOException Thrown if the requested URL can not be reached or if any other error occurs during the request.
     */
    public int getHostCode(@NotNull URL hostUrl) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) hostUrl.openConnection();
        httpURLConnection.setRequestMethod("GET");

        return httpURLConnection.getResponseCode();
    }

    /**
     * Acquire the response from a given String-URL.
     * @author Griefed
     * @param hostUrlAsString String. The URL to get the response from.
     * @return Integer. The status for the requested host.
     */
    public int getHostCode(@NotNull String hostUrlAsString) {
        try {
            return getHostCode(new URL(hostUrlAsString));
        } catch (IOException ex) {
            LOG.error("Could not get host code for: " + hostUrlAsString);
            return 418;
        }
    }

    /**
     * Acquire the status of the specified host.
     * @author Griefed
     * @param hostUrlAsString {@link String}. The URL to get the status from.
     * @return {@link String}. The status of the host. Can be either <code>OK</code>, <code>CRITICAL</code>, <code>CONNECTION REFUSED</code>,
     * <code>SSL HANDSHAKE ERROR</code>, or <code>PROTOCOL ERROR</code>.
     */
    public String getHostStatus(@NotNull String hostUrlAsString) {
        int code = 0;

        try {

            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(hostUrlAsString).openConnection();
            httpURLConnection.setRequestMethod("GET");
            code = httpURLConnection.getResponseCode();

        } catch (ProtocolException e) {
            return "PROTOCOL ERROR";
        } catch (SSLHandshakeException e) {
            return "SSL HANDSHAKE ERROR";
        } catch (MalformedURLException e) {
            return "INVALID URL";
        } catch (ConnectException e) {
            return "CONNECTION REFUSED";
        } catch (IOException e) {
            return "UNAVAILABLE";
        }

        switch (code) {
            case 301:
                return "REDIRECT";
            case 404:
                return "NOT FOUND";
            default:
                return "OK";
        }
    }

    /**
     * Acquire the IP-address of a given host.
     * @author Griefed
     * @param address {@link String} The address of the host.
     * @return {@link String} The IP-address of the host.
     */
    public String getIpOfHost(String address) {

        if (address.contains("localhost")) {
            return "127.0.0.1";
        }

        try {

            String ping = address.replace("http://","").replace("https://","");

            if (ping.contains(":")) {
                ping = ping.replace(ping.substring(ping.lastIndexOf(":")), "");
            }

            LOG.debug("Address: " + address + " IP: " + InetAddress.getByName(ping).getHostAddress());

            return InetAddress.getByName(ping).getHostAddress();

        } catch (UnknownHostException ignored) {
            LOG.error("Couldn't acquire IP for " + address);
            return null;
        }

    }

    /**
     * Check the status of a given host and IP-address.
     * @author Griefed
     * @param address {@link String} The address of the host.
     * @param ip {@link String} The IP-address of the host.
     * @param ports {@link List} Ports with which to check for host availability. Use <code>null</code> if you want to use
     *                          the default list of ports.
     * @return {@link Boolean} True if the host is reachable/available.
     */
    public boolean ping(String address, String ip, List<Integer> ports) {
        boolean available;

        try {

            available = InetAddress.getByName(address).isReachable(APPLICATION_PROPERTIES.getTimeoutConnect() * 1000);

        } catch (IOException e) {

            available = false;
        }

        if (!available) {
            try {

                available = InetAddress.getByName(ip).isReachable(APPLICATION_PROPERTIES.getTimeoutConnect() * 1000);

            } catch (IOException ignored) {

                available = false;
            }
        }

        if (!available) {

            available = checkAvailability(address, ports);

        }

        return available;
    }

    /**
     * Check the status of a given IP-address.
     * @author Griefed
     * @param ip {@link String} The IP-address of the host.
     * @param ports {@link List} Integer List. List of ports with which to check for host availability.
     * @return {@link Boolean} True if the host is reachable/available.
     */
    public boolean ping(String ip, List<Integer> ports) {
        boolean available = false;

        try {
            available = InetAddress.getByName(ip).isReachable(APPLICATION_PROPERTIES.getTimeoutConnect() * 1000);
        } catch (IOException ignored) {

        }

        if (!available) {

            LOG.debug("Ping of " + ip + " failed. Trying socket...");
            available = checkAvailability(ip, ports);

        }

        return available;
    }

    /**
     * Check the availability of a host with a socket connection and a list of ports.
     * @author Griefed
     * @param address {@link String} Address of the host. IP preferred.
     * @param ports {@link List} Integer list of ports to check with.
     * @return {@link Boolean} True if the host is reachable/available.
     */
    private boolean checkAvailability(String address, List<Integer> ports) {

        boolean available = false;

        if (address.contains(":")) {

            address = address.replace(address.substring(address.lastIndexOf(":")),"");

        }

        address = address.replace("http://","").replace("https://","");

        if (address.endsWith("/")) {
            address = address.replace(address.substring(address.lastIndexOf("/")),"");
        }

        if (ports == null || ports.size() == 0) {
            for (int port : APPLICATION_PROPERTIES.getPorts()) {

                try (Socket socket = new Socket()) {

                    socket.connect(
                            new InetSocketAddress(address, port),
                            APPLICATION_PROPERTIES.getTimeoutAvailability() * 1000);

                    available = true;
                    break;

                } catch (IOException ignored) {

                }

            }
        } else {

            if (APPLICATION_PROPERTIES.additivePorts()) {
                for (int port : APPLICATION_PROPERTIES.getPorts()) {
                    if (!ports.contains(port)) {
                        ports.add(port);
                    }
                }
            }

            LOG.debug("Ports: " + ports);

            for (int port : ports) {

                try (Socket socket = new Socket()) {

                    socket.connect(
                            new InetSocketAddress(address, port),
                            APPLICATION_PROPERTIES.getTimeoutAvailability() * 1000);

                    available = true;
                    break;

                } catch (IOException ignored) {

                }

            }
        }

        return available;
    }

}
