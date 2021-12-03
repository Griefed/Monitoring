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

import de.griefed.monitoring.ApplicationProperties;
import de.griefed.monitoring.services.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "{*}")
@RequestMapping("/api/v1")
public class SystemInformationController {

    private final InformationService INFORMATION_SERVICE;
    private final ApplicationProperties PROPERTIES;

    @Autowired
    public SystemInformationController(InformationService injectedInformationService, ApplicationProperties injectedApplicationProperties) {
        this.INFORMATION_SERVICE = injectedInformationService;
        this.PROPERTIES = injectedApplicationProperties;
    }

    @CrossOrigin(origins = "{*}")
    @RequestMapping(value = "mode", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMode() {
        return ResponseEntity.ok("{\"mode\": "+ PROPERTIES.isAgent() + "}");
    }

    @CrossOrigin(origins = "{*}")
    @RequestMapping(value = "host", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getHostInformation() {
        return ResponseEntity.ok(INFORMATION_SERVICE.retrieveHostInformation());
    }

    @CrossOrigin(origins = "{*}")
    @RequestMapping(value = "agent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAgentInformation() {
        if (!PROPERTIES.isAgent()) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(INFORMATION_SERVICE.retrieveHostInformation());
        }
    }

    @CrossOrigin(origins = "{*}")
    @RequestMapping(value = "agents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAgentsInformation() {
        if (!PROPERTIES.isAgent()) {
            return ResponseEntity.ok(INFORMATION_SERVICE.retrieveAgentsInformation());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


}
