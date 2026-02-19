package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
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

  @Async
  @Override
  public void notify(Message message) {
    if (message.getName() == null || message.getEmail() == null || message.getMessage() == null) {
      logger.warn("Message content is incomplete. Skipping notification.");
      return;
    }
    String subject = "New Message from " + message.getName();
    String plainTextBody = String.format(
            "You have received a new message:\n\nName: %s\nEmail: %s\nMessage: %s",
            message.getName(),
            message.getEmail(),
            message.getMessage()
    );
    String htmlBody = String.format(
            "<html>" +
                    "<body>" +
                    "<h1>New Message Notification</h1>" +
                    "<p>You have received a new message:</p>" +
                    "<p><strong>Name:</strong> %s</p>" +
                    "<p><strong>Email:</strong> %s</p>" +
                    "<p><strong>Message:</strong><br>%s</p>" +
                    "<hr>" +
                    "<p>" +
                    "Thank you for reaching out to us. We'll review your message and respond soon." +
                    "</p>" +
                    "<p>" +
                    "If you prefer not to receive further emails, you can " +
                    "<a href=\"https://www.lilianagorga.org/unsubscribe\">unsubscribe here</a>." +
                    "</p>" +
                    "<p>" +
                    "Best regards,<br>" +
                    "Team Liliana Gorga" +
                    "</p>" +
                    "<p style=\"font-size: 12px; color: gray;\">" +
                    "This message was generated automatically. For any questions, contact us at " +
                    "<a href=\"mailto:info@lilianagorga.org\">info@lilianagorga.org</a>." +
                    "</p>" +
                    "</body>" +
                    "</html>",
            message.getName(),
            message.getEmail(),
            message.getMessage().replace("\n", "<br>")
    );
    try {
      emailService.sendEmail(adminEmail, subject, plainTextBody, htmlBody);
      logger.info("Email notification sent for message ID: {}", message.getId());
    } catch (Exception e) {
      logger.error("Failed to send email notification for message ID: {}", message.getId(), e);
    }
  }
}