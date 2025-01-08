package dev.lilianagorga.mywebsite.repository;

import dev.lilianagorga.mywebsite.entity.Message;
import dev.lilianagorga.mywebsite.AbstractTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MessageRepositoryTest extends AbstractTestConfig {

  @Autowired
  private MessageRepository messageRepository;

  private Message testMessage;

  @BeforeEach
  void setUp() {
    messageRepository.deleteAll();

    testMessage = Message.builder()
            .name("Test Sender")
            .email("sender@example.com")
            .message("Hello, this is a test message!")
            .createdAt(LocalDateTime.now())
            .build();

    messageRepository.save(testMessage);
  }

  @AfterEach
  void cleanup() {
    messageRepository.deleteAll();
  }

  @Test
  void testRetrieveMessage() {
    List<Message> allMessages = messageRepository.findAll();
    assertThat(allMessages).hasSize(1);
    assertThat(allMessages.getFirst().getName()).isEqualTo("Test Sender");
  }

  @Test
  void testDeleteMessage() {
    messageRepository.deleteById(testMessage.getId());
    List<Message> allMessages = messageRepository.findAll();
    assertThat(allMessages).isEmpty();
  }
}