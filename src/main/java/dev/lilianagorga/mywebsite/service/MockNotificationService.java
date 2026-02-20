package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "test"})
public class MockNotificationService implements NotificationService {

  private static final Logger logger = LoggerFactory.getLogger(MockNotificationService.class);

  @Override
  public void notify(Message message) {
    logger.info("MockNotificationService using - Notification simulated");
    logger.info("Mock notification: Message received from {} ({}) in profile 'dev/test'. No email sent.",
            message.getName(), message.getEmail());
  }
}