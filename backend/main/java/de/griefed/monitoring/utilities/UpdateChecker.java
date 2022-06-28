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
import de.griefed.versionchecker.github.GitHubChecker;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Initialize our GitHub and GitLab instances with the corresponding repository addresses, so we can
 * then run our update checks later on.
 *
 * @author Griefed
 */
@Service
public class UpdateChecker {

  private static final Logger LOG = LogManager.getLogger(UpdateChecker.class);
  private final ApplicationProperties APPLICATIONPROPERTIES;
  private GitHubChecker GITHUB;
  private String updater;

  /**
   * Constructor for Dependency Injection.
   *
   * @author Griefed
   */
  @Autowired
  public UpdateChecker(ApplicationProperties injectedApplicationProperties) {

    this.APPLICATIONPROPERTIES = injectedApplicationProperties;

    try {
      this.GITHUB = new GitHubChecker("Griefed/Monitoring").refresh();
    } catch (IOException ex) {
      LOG.error("The GitHub user/repository you set resulted in a malformed URL.", ex);
      this.GITHUB = null;
    }

    this.updater = checkForUpdate(APPLICATIONPROPERTIES.getVersion(), false);
  }

  /**
   * Check for available updates.
   *
   * @author Griefed
   */
  public void check() {
    refresh();
    this.updater = checkForUpdate(APPLICATIONPROPERTIES.getVersion(), false);
  }

  /**
   * Getter for the updater which can contain information about the latest available update, if one
   * is available;
   *
   * @author Griefed
   * @return {@link String} Return information about the latest available update, if available.
   */
  public String getUpdater() {
    return updater;
  }

  /**
   * Refresh the GitHub, GitLab and GitGriefed instances, so we get the most current releases.
   *
   * @author Griefed
   * @return {@link UpdateChecker} reference.
   */
  public UpdateChecker refresh() {
    try {
      this.GITHUB.refresh();
    } catch (Exception ex) {
      LOG.error("Error refreshing GitHub.", ex);
      this.GITHUB = null;
    }

    return this;
  }

  /**
   * Check our GitLab, GitGriefed and GitHub instances for updates, sequentially, and then return
   * the update.
   *
   * @author Griefed
   * @param version {@link String} The version for which to check for updates.
   * @param preReleaseCheck {@link Boolean} Whether to check pre-releases as well. Use <code>true
   *     </code> to check pre-releases as well, <Code>false</Code> to only check with regular
   *     releases.
   * @return {@link String} The update, if available, as well as the download URL.
   */
  public String checkForUpdate(@NotNull String version, Boolean preReleaseCheck) {

    if (version.equalsIgnoreCase("dev")) {
      return "No updates available.";
    }

    String updater = null;

    if (GITHUB != null) {
      LOG.debug("Checking Github for updates...");

      // Check GitHub for the most recent release.
      updater = GITHUB.checkForUpdate(version, preReleaseCheck);
    }

    if (updater == null) {
      return "No updates available.";
    } else {

      if (updater.contains(";")) {
        LOG.info("Update available!");
        LOG.info("    " + updater.split(";")[0]);
        LOG.info("    " + updater.split(";")[1]);
      } else {
        LOG.info("No updates available.");
      }

      return updater;
    }
  }
}
