package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class EmailNotificationService implements NotificationService {

  private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
  private final EmailService emailService;
  private final String adminEmail;

  public EmailNotificationService(EmailService emailService, @Value("${admin.email}") String adminEmail) {
    this.emailService = emailService;
    this.adminEmail = adminEmail;
  }

  @Override
  public void notify(Message message) {
    if (message.getName() == null || message.getEmail() == null || message.getMessage() == null) {
      logger.warn("Message content is incomplete. Skipping notification.");
      return;
    }
    String subject = "New Message from " + message.getName();
    String body = String.format(
            "You have received a new message:\n\nName: %s\nEmail: %s\nMessage: %s",
            message.getName(),
            message.getEmail(),
            message.getMessage()
    );
    try {
      emailService.sendEmail(adminEmail, subject, body);
      logger.info("Email notification sent for message ID: {}", message.getId());
    } catch (Exception e) {
      logger.error("Failed to send email notification for message ID: {}", message.getId(), e);
    }
  }
}