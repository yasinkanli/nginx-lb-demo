package com.yasinkanli.microservice.nginxlb.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @Value("${INSTANCE_NAME:local}")
  private String instanceName;

  @GetMapping("/hello")
  public String hello() {
    return "Response Instance: " + instanceName;
  }
}
