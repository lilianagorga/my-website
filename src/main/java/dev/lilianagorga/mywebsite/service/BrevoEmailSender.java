package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.exception.EmailSendingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Profile("prod")
public class BrevoEmailSender implements EmailSender {

  private final RestTemplate restTemplate;
  private final String senderEmail;
  private final String apiKey;
  private final String apiUrl;

  public BrevoEmailSender(RestTemplate restTemplate,
                           @Value("${site.owner.email}") String senderEmail,
                           @Value("${brevo.api.key}") String apiKey,
                           @Value("${brevo.api.url}") String apiUrl) {
    this.restTemplate = restTemplate;
    this.senderEmail = senderEmail;
    this.apiKey = apiKey;
    this.apiUrl = apiUrl;
  }

  @Override
  public String sendEmail(String to, String subject, String plainTextBody, String htmlBody) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("api-key", apiKey);

      Map<String, Object> body = Map.of(
          "sender", Map.of("email", senderEmail),
          "to", List.of(Map.of("email", to)),
          "subject", subject,
          "textContent", plainTextBody,
          "htmlContent", htmlBody,
          "headers", Map.of("List-Unsubscribe",
              "<mailto:unsubscribe@lilianagorga.org>, <https://www.lilianagorga.org/unsubscribe>")
      );

      HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
      restTemplate.postForEntity(apiUrl, request, String.class);
      return "Email sent successfully";
    } catch (Exception ex) {
      throw new EmailSendingException("Failed to send email via Brevo API", ex);
    }
  }
}