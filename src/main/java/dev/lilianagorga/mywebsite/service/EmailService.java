package dev.lilianagorga.mywebsite.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import dev.lilianagorga.mywebsite.exception.EmailSendingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class EmailService {

  private final SendGrid sendGrid;
  private final String activeProfile;

  public EmailService(@Value("${sendgrid.api.key}") String apiKey,
                      @Value("${spring.profiles.active}") String activeProfile) {
    this.sendGrid = new SendGrid(apiKey);
    this.activeProfile = activeProfile;
  }

  public String sendEmail(String to, String subject, String body) {

    if ("dev".equals(activeProfile) || "test".equals(activeProfile)) {
      return "Email not sent (Mocked). Active profile: " + activeProfile;
    }

    Email fromEmail = new Email("gorgaliliana87@gmail.com");
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
      throw new EmailSendingException("Failed to send email", ex);
    }
  }
}