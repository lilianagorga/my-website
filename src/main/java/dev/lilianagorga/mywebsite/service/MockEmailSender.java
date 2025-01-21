package dev.lilianagorga.mywebsite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "test"})
public class MockEmailSender implements EmailSender {

  private static final Logger logger = LoggerFactory.getLogger(MockEmailSender.class);

  @Override
  public String sendEmail(String to, String subject, String plainTextBody, String htmlBody) {
    String listUnsubscribeHeader = "<mailto:unsubscribe@lilianagorga.org>, <https://www.lilianagorga.org/unsubscribe>";
    logger.info("MockEmailSender: Email not sent (mocked) to {} with subject {}. Plain text body: {}, HTML body: {}, List-Unsubscribe Header: {}",
            to, subject, plainTextBody, htmlBody, listUnsubscribeHeader);
    return "Email not sent (mocked).";
  }
}