package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class EmailNotificationService implements NotificationService {

  private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
  private final EmailService emailService;

  public EmailNotificationService(EmailService emailService) {
    this.emailService = emailService;
    System.out.println("EmailNotificationService initialized - using real email notifications");
    logger.info("EmailNotificationService initialized");
  }

  @Override
  public void notify(Message message) {
    String subject = "New Message Received";
    String body = String.format("You have received a new message from %s (%s):\n\n%s",
            message.getName(), message.getEmail(), message.getMessage());

    emailService.sendEmail("your-email@example.com", subject, body);
    logger.info("Email notification sent for message ID: {}", message.getId());
  }
}