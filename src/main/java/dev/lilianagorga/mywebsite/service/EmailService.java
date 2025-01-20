package dev.lilianagorga.mywebsite.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final EmailSender emailSender;

  public EmailService(EmailSender emailSender) {
    this.emailSender = emailSender;
  }

  public String sendEmail(String to, String subject, String plainTextBody, String htmlBody) {
    return emailSender.sendEmail(to, subject, plainTextBody, htmlBody);
  }
}