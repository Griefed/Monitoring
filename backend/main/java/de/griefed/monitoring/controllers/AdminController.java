package de.griefed.monitoring.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import de.griefed.monitoring.ApplicationProperties;
import de.griefed.monitoring.model.Configuration;
import de.griefed.monitoring.utilities.JsonUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping(
    value = "/api/v1/admin",
    method = {RequestMethod.GET, RequestMethod.POST})
public class AdminController {

  private static final Logger LOG = LogManager.getLogger(AdminController.class);

  private final ApplicationProperties APPLICATIONPROPERTIES;
  private final JsonUtilities JSON_UTILITIES;

  @Autowired
  public AdminController(
      ApplicationProperties injectedApplicationProperties, JsonUtilities injectedJsonUtilities) {
    this.APPLICATIONPROPERTIES = injectedApplicationProperties;
    this.JSON_UTILITIES = injectedJsonUtilities;
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

    LOG.info(configuration.toString());

    return "ok";
  }

  @CrossOrigin(origins = {"*"})
  @GetMapping
  public ResponseEntity<String> set(@RequestParam(value = "configuration") String configuration) {

    LOG.info(configuration);

    try {
      JsonNode confJson = JSON_UTILITIES.getJson(configuration);
      LOG.info(confJson.get("settings"));
      LOG.info(confJson.get("hosts"));
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .build();
    } catch (Exception ex) {
      return ResponseEntity.badRequest().build();
    }
  }
}
