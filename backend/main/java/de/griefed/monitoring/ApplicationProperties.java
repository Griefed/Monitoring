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

import com.fasterxml.jackson.databind.JsonNode;
import de.griefed.monitoring.utilities.JsonUtilities;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Class responsible for handling our properties. Loaded at boot in {@link Main}.
 * @author Griefed
 */
@Component
public class ApplicationProperties extends Properties {

    private static final Logger LOG = LogManager.getLogger(ApplicationProperties.class);

    private final File HOSTS_FILE = new File("hosts.json");

    private final JsonUtilities JSON_UTILITIES;

    /**
     * Seconds to wait until a connection timeout is triggered for getting information from hosts.
     */
    @Value("${de.griefed.monitoring.timeout.connect}")
    private int timeoutConnect = 5;

    /**
     * Seconds to wait until a read timeout is triggered for getting information from hosts.
     */
    @Value("${de.griefed.monitoring.timeout.availability}")
    private int timeoutAvailability = 5;

    /**
     * Number of threads used for updating host information.
     */
    @Value("${de.griefed.monitoring.thread.count}")
    private int threadCount = 40;

    /**
     * Constructor for our properties. Sets a couple of default values for use in Monitoring.
     * @author Griefed
     * @param injectedJsonUtilities Instance of {@link JsonUtilities}.
     */
    @Autowired
    public ApplicationProperties(JsonUtilities injectedJsonUtilities) {
        this.JSON_UTILITIES = injectedJsonUtilities;

        if (!new File("application.properties").exists()) {
            try {
                FileUtils.copyInputStreamToFile(
                        Objects.requireNonNull(Main.class.getResourceAsStream("/" + new File("application.properties").getName())),
                        new File("application.properties"));
            } catch (IOException ex) {
                LOG.error("Error creating file: " + new File("application.properties"), ex);
            }
        }

        // Load the properties file from the classpath, providing default values.
        try (InputStream inputStream = new ClassPathResource("application.properties").getInputStream()) {
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

        this.threadCount = Integer.parseInt(getProperty("de.griefed.monitoring.thread.count","40"));
    }

    /**
     * Getter for a list of hosts to gather information from.
     * @author Griefed
     * @return List String. Each entry is a combination of ip and access token in the format <code>host_ip1,access_token1</code>.
     * @throws IOException when hosts can not be read from the file.
     */
    public JsonNode getHosts() throws IOException {
        return JSON_UTILITIES.getJson(HOSTS_FILE).get("hosts");
    }

    /**
     * Getter for a list of ports to scan in {@link de.griefed.monitoring.services.InformationService} for host availability.
     * @author Griefed
     * @return List int. Returns a list of ports which will be scanned to determine host availability.
     */
    public List<Integer> getPorts() {

        List<Integer> ports = new ArrayList<>(1000);

        if (getProperty("de.griefed.monitoring.host.ports").contains(",")) {

            List<String> portsAsStrings = new ArrayList<String>(
                    Arrays.asList(
                            getProperty("de.griefed.monitoring.host.ports").split(",")));

            portsAsStrings.forEach(entry -> ports.add(Integer.parseInt(entry)));

        } else {

            ports.add(Integer.parseInt(getProperty("de.griefed.monitoring.host.ports")));

        }

        return ports;
    }

    /**
     * Getter for whether default ports should be added to host-configured ports.
     * @author Griefed
     * @return {@link Boolean} True if default ports should be added to host-configured ports.
     */
    public Boolean additivePorts() {
        return Boolean.parseBoolean(getProperty("de.griefed.ports.additive"));
    }

    /**
     * Getter for the number of seconds to wait until a connection timeout is triggered for getting information from hosts.
     * @author Griefed
     * @return Integer. Returns the number of seconds as an int.
     */
    public int getTimeoutConnect() {
        return timeoutConnect;
    }

    /**
     * Getter for the number of seconds to wait until a read timeout is triggered for getting information from hosts.
     * @author Griefed
     * @return Integer. Returns the number of seconds as an int.
     */
    public int getTimeoutAvailability() {
        return timeoutAvailability;
    }

    /**
     * Getter for the number of threads used for updating host information.
     * @author Griefed
     * @return {@link Integer} The number of threads used for updating host information.
     */
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * The polling rate at which to update the webfrontend.
     * @author Griefed
     * @return {@link Integer} The polling rate at which to update the webfrontend.
     */
    public int getPollingRate() {
        return Integer.parseInt(getProperty("de.griefed.monitoring.polling", "5000"));
    }
}
