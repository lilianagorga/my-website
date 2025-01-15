package dev.lilianagorga.mywebsite.config;

import dev.lilianagorga.mywebsite.service.NotificationService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceInitializer {

  private final NotificationService notificationService;

  public NotificationServiceInitializer(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @PostConstruct
  public void init() {
    System.out.println("Using NotificationService implementation: " + notificationService.getClass().getSimpleName());
  }
}