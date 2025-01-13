package dev.lilianagorga.mywebsite.config;

import dev.lilianagorga.mywebsite.service.EmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {

  @Bean
  public EmailService emailService() {
    return mock(EmailService.class);
  }
}