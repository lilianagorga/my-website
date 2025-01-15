package dev.lilianagorga.mywebsite.config;

import dev.lilianagorga.mywebsite.service.EmailSender;
import dev.lilianagorga.mywebsite.service.EmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {

  @Bean
  public EmailSender emailSender() {
    return mock(EmailSender.class);
  }
  @Bean
  public EmailService emailService(EmailSender emailSender) {
    return new EmailService(emailSender);
  }
}