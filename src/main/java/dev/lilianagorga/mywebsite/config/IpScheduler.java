package dev.lilianagorga.mywebsite.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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

  @EventListener(ApplicationReadyEvent.class)
  public void onStartup() {
    try {
      log.info("Startup IP update triggered");
      boolean success = ipUpdater.updateIpAddress();
      if (success) {
        log.info("Startup IP update completed successfully");
      } else {
        log.warn("Startup IP update failed after retries");
      }
    } catch (Exception e) {
      log.error("Startup IP update failed: {}", e.getMessage(), e);
    }
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
