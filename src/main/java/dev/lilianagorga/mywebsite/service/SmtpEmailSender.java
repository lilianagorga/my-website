package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class SmtpEmailSender implements EmailSender {

  private final JavaMailSender mailSender;
  private final String senderEmail;

  public SmtpEmailSender(JavaMailSender mailSender, @Value("${site.owner.email}") String senderEmail) {
    this.mailSender = mailSender;
    this.senderEmail = senderEmail;
  }

  @Override
  public String sendEmail(String to, String subject, String plainTextBody, String htmlBody) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(senderEmail);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(plainTextBody, htmlBody);

      message.addHeader("List-Unsubscribe",
          "<mailto:unsubscribe@lilianagorga.org>, <https://www.lilianagorga.org/unsubscribe>");

      mailSender.send(message);
      return "Email sent successfully";
    } catch (MessagingException ex) {
      throw new EmailSendingException("Failed to send email", ex);
    }
  }
}