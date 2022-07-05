package de.griefed.monitoring.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import de.griefed.monitoring.ApplicationProperties;
import de.griefed.monitoring.model.request.Configuration;
import de.griefed.monitoring.services.AdministrationService;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping(
    value = "/api/v1/admin",
    method = {RequestMethod.GET, RequestMethod.POST})
public class AdminController {

  private static final Logger LOG = LogManager.getLogger(AdminController.class);

  private final ApplicationProperties APPLICATIONPROPERTIES;
  private final AdministrationService ADMIN_SERVICE;

  @Autowired
  public AdminController(ApplicationProperties injectedApplicationProperties, AdministrationService injectedAdminService) {
    this.APPLICATIONPROPERTIES = injectedApplicationProperties;
    this.ADMIN_SERVICE = injectedAdminService;
  }

  @CrossOrigin(origins = {"*"})
  @RequestMapping(
      path = "/get",
      produces = MediaType.APPLICATION_JSON_VALUE,
      method = RequestMethod.GET)
  public ResponseEntity<JsonNode> get() throws IOException {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .body(APPLICATIONPROPERTIES.getAdminSettings());
  }

  @CrossOrigin(origins = {"*"})
  @PutMapping("/")
  public String save(@RequestBody Configuration configuration) {

    LOG.info(configuration);
    ADMIN_SERVICE.safeConfiguration(configuration);

    return "ok";
  }
}
