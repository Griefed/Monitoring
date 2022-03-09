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
package de.griefed.monitoring.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import de.griefed.monitoring.ApplicationProperties;
import de.griefed.monitoring.services.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController providing endpoints for retrieving information about the host and configured hosts, if any.
 * @author Griefed
 */
@RestController
@CrossOrigin(origins = "{*}")
@RequestMapping("/api/v1")
public class InformationController {

    private final InformationService INFORMATION_SERVICE;
    private final ApplicationProperties PROPERTIES;

    /**
     * Constructor responsible for DI.
     * @author Griefed
     * @param injectedInformationService Instance of {@link InformationService}.
     * @param injectedApplicationProperties Instance of {@link ApplicationProperties}.
     */
    @Autowired
    public InformationController(InformationService injectedInformationService, ApplicationProperties injectedApplicationProperties) {
        this.INFORMATION_SERVICE = injectedInformationService;
        this.PROPERTIES = injectedApplicationProperties;
    }

    /**
     * GET endpoint for retrieving the polling rate of this instance. Returns, as JSON, the polling rate currently set.
     * @author Griefed
     * @return String. <code>de.griefed.monitoring.polling</code> wrapped in a ResponseEntity as application/json.
     */
    @CrossOrigin(origins = "{*}")
    @RequestMapping(value = "polling", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPolling() {
        return ResponseEntity.ok("{\"polling\": "+ PROPERTIES.getPollingRate() + "}");
    }

    /**
     * GET endpoint for retrieving information about all configured hosts.<br> <code>de.griefed.monitoring.host</code>
     * must be set to <code>false</code> in order for this endpoint to return information, otherwise a <code>400</code>-error
     * is returned.<br>
     * @author Griefed
     * @return String in JSON format. Information about all configured hosts. Wrapped in a ResponseEntity as application/json.
     */
    @CrossOrigin(origins = "{*}")
    @RequestMapping(value = "hosts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> getHostsInformation() {
        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        "application/json")
                .body(INFORMATION_SERVICE.retrieveHostsInformation()
                );
    }
}
