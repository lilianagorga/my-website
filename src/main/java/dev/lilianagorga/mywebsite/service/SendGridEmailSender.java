package dev.lilianagorga.mywebsite.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
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
  public String sendEmail(String to, String subject, String plainTextBody, String htmlBody) {
    Email fromEmail = new Email(senderEmail);
    Email toEmail = new Email(to);
    Content plainTextContent = new Content("text/plain", plainTextBody);
    Content htmlContent = new Content("text/html", htmlBody);

    Mail mail = new Mail();
    mail.setFrom(fromEmail);
    mail.setSubject(subject);
    mail.addPersonalization(new Personalization() {{
      addTo(toEmail);
    }});
    mail.addContent(plainTextContent);
    mail.addContent(htmlContent);
    mail.addHeader("List-Unsubscribe", "<mailto:unsubscribe@lilianagorga.org>, <https://www.lilianagorga.org/unsubscribe>");


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