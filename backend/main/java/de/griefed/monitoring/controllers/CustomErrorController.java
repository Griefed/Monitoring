package de.griefed.monitoring.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Custom error controller which redirects to our /whoops page in case of any error.
 *
 * @author Griefed
 */
@Controller
public class CustomErrorController implements ErrorController {

  /**
   * Redirect all unknown paths to our error-page.
   *
   * @author Griefed
   * @return Redirects the requester to our error page.
   */
  @RequestMapping(value = "/error")
  public String error() {
    return "forward:/";
  }
}
