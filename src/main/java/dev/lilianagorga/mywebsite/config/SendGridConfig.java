package dev.lilianagorga.mywebsite.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class SendGridConfig {

  @Value("${sendgrid.api.key}")
  private String apiKey;

  @Bean
  public SendGrid sendGridClient() {
    return new SendGrid(apiKey);
  }
}