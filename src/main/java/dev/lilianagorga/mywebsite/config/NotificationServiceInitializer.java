package dev.lilianagorga.mywebsite.config;

import dev.lilianagorga.mywebsite.service.NotificationService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceInitializer {

  private static final Logger logger = LoggerFactory.getLogger(NotificationServiceInitializer.class);
  private final NotificationService notificationService;

  public NotificationServiceInitializer(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @PostConstruct
  public void init() {
    logger.info("Using NotificationService implementation: {}", notificationService.getClass().getSimpleName());
  }
}