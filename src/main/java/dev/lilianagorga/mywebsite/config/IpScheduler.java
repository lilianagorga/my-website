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
    // IP updater disabled - using 0.0.0.0/0 whitelist on Atlas
    log.info("IP updater disabled - using 0.0.0.0/0 whitelist");
  }

  @Scheduled(cron = "0 0 9 * * *")
  public void updateIp() {
    // IP updater disabled - using 0.0.0.0/0 whitelist on Atlas
    log.info("IP updater disabled - using 0.0.0.0/0 whitelist");
  }
}
