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
    public EmailService emailService() {
      return Mockito.mock(EmailService.class);
    }
  }

  @Autowired
  private EmailService emailService;

  @Test
  void shouldReturnMockedMessageInTestProfile() {
    Mockito.when(emailService.sendEmail("test@example.com", "Subject", "Body"))
            .thenReturn("Email not sent (Mocked). Active profile: test");
    String response = emailService.sendEmail("test@example.com", "Subject", "Body");
    assertEquals("Email not sent (Mocked). Active profile: test", response);
  }
}