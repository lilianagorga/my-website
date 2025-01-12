package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import dev.lilianagorga.mywebsite.entity.Message;
import dev.lilianagorga.mywebsite.repository.MessageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MessageServiceTest extends AbstractTestConfig {

  @Autowired
  private MessageService messageService;

  @Autowired
  private MessageRepository messageRepository;

  private Message testMessage;

  @BeforeEach
  void setUp() {
    messageRepository.deleteAll();
    testMessage = Message.builder()
            .name("Test User")
            .email("test@example.com")
            .message("Hello World")
            .build();
    messageRepository.save(testMessage);
  }

  @AfterEach
  void tearDown() {
    messageRepository.deleteAll();
  }

  @Test
  void createMessage() {
    Message newMessage = Message.builder()
            .name("New User")
            .email("new@example.com")
            .message("New Message")
            .build();
    Message createdMessage = messageService.createMessage(newMessage);

    assertNotNull(createdMessage.getId());
    assertEquals("New User", createdMessage.getName());
  }

  @Test
  void getMessageById() {
    Optional<Message> message = messageService.getMessageById(testMessage.getId());
    assertTrue(message.isPresent());
    assertEquals("Test User", message.get().getName());
  }

  @Test
  void getMessageByEmail() {
    Optional<Message> message = messageService.getMessageByEmail("test@example.com");
    assertTrue(message.isPresent());
    assertEquals("Hello World", message.get().getMessage());
  }

  @Test
  void getAllMessages() {
    List<Message> messages = messageService.getAllMessages();
    assertEquals(1, messages.size());
  }

  @Test
  void updateMessage() {
    testMessage.setMessage("Updated Message");
    Message updatedMessage = messageService.updateMessage(testMessage.getId(), testMessage);
    assertEquals("Updated Message", updatedMessage.getMessage());
  }

  @Test
  void deleteMessage() {
    messageService.deleteMessage(testMessage.getId());
    Optional<Message> message = messageRepository.findById(testMessage.getId());
    assertTrue(message.isEmpty());
  }
}