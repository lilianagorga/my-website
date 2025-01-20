package dev.lilianagorga.mywebsite.service;

public interface EmailSender {
  String sendEmail(String to, String subject, String plainTextBody, String htmlBody);
}