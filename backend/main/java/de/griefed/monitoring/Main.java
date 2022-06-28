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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class. Copies the log4j2.xml and application.properties from the JAR to the base directory
 * where this program is being executed in.
 *
 * @author Griefed
 */
public class Main {

  private static final Logger LOG = LogManager.getLogger(Main.class);

  private static final File log4j2xml = new File("log4j2.xml");

  private static final File properties = new File("application.properties");

  /**
   * Runs Monitoring, creates files, then starts the Spring Boot application.
   *
   * @author Griefed
   * @param args Arguments passed from the commandline. Overwritten later on, so it serves no
   *     practical use in Monitoring.
   */
  public static void main(String[] args) {

    // Create our log4j2.xml and application.properties
    createFile(log4j2xml);
    createFile(properties);

    Properties properties = new Properties();

    try (InputStream inputStream = new FileInputStream("application.properties")) {
      new Properties();
      properties.load(inputStream);
    } catch (IOException ex) {
      LOG.error("Couldn't read properties file.", ex);
    }

    args = new String[] {properties.getProperty("de.griefed.monitoring.agent", "false")};

    MonitoringJavaApplication.main(args);
  }

  /**
   * Copy a file from inside our JAR to the host filesystem.
   *
   * @author Griefed
   * @param fileToCreate File. The file in the JAR to create on the host filesystem.
   */
  private static void createFile(File fileToCreate) {
    if (!fileToCreate.exists()) {
      try {
        FileUtils.copyInputStreamToFile(
            Objects.requireNonNull(Main.class.getResourceAsStream("/" + fileToCreate.getName())),
            fileToCreate);
      } catch (IOException ex) {
        LOG.error("Error creating file: " + fileToCreate, ex);
      }
    }
  }
}
