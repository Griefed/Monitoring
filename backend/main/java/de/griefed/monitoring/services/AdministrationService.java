package de.griefed.monitoring.services;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.griefed.monitoring.ApplicationProperties;
import de.griefed.monitoring.model.Configuration;
import de.griefed.monitoring.model.Hosts;
import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdministrationService {

  private static final Logger LOG = LogManager.getLogger(AdministrationService.class);

  private final ApplicationProperties APPLICATION_PROPERTIES;
  private final ObjectWriter OBJECT_WRITER;

  @Autowired
  public AdministrationService(ApplicationProperties injectedApplicationProperties) {
    this.APPLICATION_PROPERTIES = injectedApplicationProperties;

    OBJECT_WRITER = new ObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .writer(new DefaultPrettyPrinter());
  }

  public void safeConfiguration(Configuration configuration) {
    APPLICATION_PROPERTIES.saveSettings(configuration.getSettings());
    saveHosts(new Hosts(configuration.getHosts()));
  }

  private void saveHosts(Hosts hosts) {
    try {
      OBJECT_WRITER.writeValue(APPLICATION_PROPERTIES.getHostsFile(), hosts);
    } catch (IOException ex) {
      LOG.error("Couldn't store hosts.",ex);
    }
  }
}
