package dev.lilianagorga.mywebsite.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Profile("prod")
public class SendGridEmailSender implements EmailSender {

  private final SendGrid sendGrid;
  private final String senderEmail;

  public SendGridEmailSender(SendGrid sendGrid, @Value("${site.owner.email}") String senderEmail) {
    this.sendGrid = sendGrid;
    this.senderEmail = senderEmail;
  }

  @Override
  public String sendEmail(String to, String subject, String body) {
    Email fromEmail = new Email(senderEmail);
    Email toEmail = new Email(to);
    Content content = new Content("text/plain", body);
    Mail mail = new Mail(fromEmail, subject, toEmail, content);

    Request request = new Request();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      Response response = sendGrid.api(request);
      return "Email sent! Status Code: " + response.getStatusCode();
    } catch (IOException ex) {
      throw new RuntimeException("Failed to send email", ex);
    }
  }
}