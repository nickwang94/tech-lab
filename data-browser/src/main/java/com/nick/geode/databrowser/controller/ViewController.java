package com.nick.geode.databrowser.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving the main web UI.
 */
@Controller
public class ViewController {

  @GetMapping("/")
  public String index() {
    return "index";
  }
}
