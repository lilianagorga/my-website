package dev.lilianagorga.mywebsite.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceTest {

  @Configuration
  static class TestConfig {
    @Bean
    public EmailSender emailSender() {
      return Mockito.mock(EmailSender.class);
    }
    @Bean
    public EmailService emailService(EmailSender emailSender) {
      return new EmailService(emailSender);
    }
  }

  @Autowired
  private EmailService emailService;

  @Autowired
  private EmailSender emailSender;

  @Test
  void shouldReturnMockedMessageInTestProfile() {
    Mockito.when(emailSender.sendEmail(
            "test@example.com",
            "Subject",
            "Body",
            "<p>Body</p>"
    )).thenReturn("Email not sent (Mocked). Active profile: test");

    String response = emailService.sendEmail(
            "test@example.com",
            "Subject",
            "Body",
            "<p>Body</p>"
    );
    assertEquals("Email not sent (Mocked). Active profile: test", response);
  }
}