package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.config.MongoDBIpUpdater;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IpController {

  private final MongoDBIpUpdater ipUpdater;

  public IpController(MongoDBIpUpdater ipUpdater) {
    this.ipUpdater = ipUpdater;
  }

  @GetMapping("/update-ip")
  public String updateIpManually() {
    ipUpdater.updateIpAddress();
    return "IP updated!";
  }
}