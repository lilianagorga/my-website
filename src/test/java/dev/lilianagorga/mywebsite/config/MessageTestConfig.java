package dev.lilianagorga.mywebsite.config;

import dev.lilianagorga.mywebsite.service.MessageService;
import dev.lilianagorga.mywebsite.service.NotificationService;
import dev.lilianagorga.mywebsite.repository.MessageRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageTestConfig {

  @Bean
  public NotificationService notificationService() {
    return Mockito.mock(NotificationService.class);
  }

  @Bean
  public MessageService messageService(MessageRepository messageRepository, NotificationService notificationService) {
    return new MessageService(messageRepository, notificationService);
  }
}