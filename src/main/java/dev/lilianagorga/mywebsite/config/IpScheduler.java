package dev.lilianagorga.mywebsite.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class IpScheduler {

  private final MongoDBIpUpdater ipUpdater;

  public IpScheduler(MongoDBIpUpdater ipUpdater) {
    this.ipUpdater = ipUpdater;
  }

  @Scheduled(cron = "0 0 9 * * *")
  public void updateIp() {
    try {
      log.info("Starting scheduled IP update...");
      ipUpdater.updateIpAddress();
    } catch (Exception e) {
      log.error("Failed to update IP: ", e);
    }
  }
}
