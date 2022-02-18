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
import de.griefed.monitoring.services.InformationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class Schedules {

    private static final Logger LOG = LogManager.getLogger(Schedules.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final InformationService INFORMATION_SERVICE;
    private final ApplicationProperties PROPERTIES;

    public Schedules(InformationService injectedInformationService, ApplicationProperties injectedApplicationProperties) {
        this.INFORMATION_SERVICE = injectedInformationService;
        this.PROPERTIES = injectedApplicationProperties;
    }

    /**
     * Periodically check and update the status information for the configured hosts. Set <code>de.griefed.monitoring.schedule.hosts</code>
     * to your desired CRON expression.
     * @author Griefed
     */
    @Scheduled(cron = "${de.griefed.monitoring.schedule.hosts}")
    public void refreshHosts() {
        LOG.debug("Current Time: " + dateFormat.format(new Date()) + " - Refreshing hosts information.");
        INFORMATION_SERVICE.poll();
    }
}
