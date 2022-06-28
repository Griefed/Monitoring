package de.griefed.monitoring.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import de.griefed.monitoring.ApplicationProperties;
import de.griefed.monitoring.model.Settings;
import de.griefed.monitoring.utilities.JsonUtilities;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/api/v1/admin")
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
  @GetMapping(path = "get", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<JsonNode> get() throws IOException {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .body(APPLICATIONPROPERTIES.getAdminSettings());
  }

  @CrossOrigin(origins = {"*"})
  @PostMapping("/save")
  public @ResponseBody String save(@RequestBody Settings settings) {

    LOG.info(settings.toString());

    /*try {
        JsonNode confJson = JSON_UTILITIES.getJson(configuration);
        LOG.info(confJson.get("settings"));
        LOG.info(confJson.get("hosts"));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    } catch (Exception ex) {
        return ResponseEntity.badRequest().build();
    }*/

    return "ok";
  }

  @CrossOrigin(origins = {"*"})
  @GetMapping("set")
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
