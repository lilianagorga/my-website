package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import dev.lilianagorga.mywebsite.config.MessageTestConfig;
import dev.lilianagorga.mywebsite.entity.Message;
import dev.lilianagorga.mywebsite.repository.MessageRepository;
import dev.lilianagorga.mywebsite.service.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(MessageTestConfig.class)
class MessageControllerTest extends AbstractTestConfig {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private NotificationService notificationService;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {

    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    messageRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    messageRepository.deleteAll();
  }

  @Test
  void shouldCreateMessage() throws Exception {
    String messageJson = """
                {
                    "name": "Test User",
                    "email": "test@example.com",
                    "message": "This is a test message"
                }
            """;

    mockMvc.perform(post("/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(messageJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    verify(notificationService).notify(messageCaptor.capture());
    Message capturedMessage = messageCaptor.getValue();
    assertThat(capturedMessage.getName()).isEqualTo("Test User");
    assertThat(capturedMessage.getEmail()).isEqualTo("test@example.com");
    assertThat(capturedMessage.getMessage()).isEqualTo("This is a test message");
    assertThat(capturedMessage.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldGetAllMessages() throws Exception {
    messageRepository.save(Message.builder()
            .name("Test User")
            .email("test@example.com")
            .message("This is a test message")
            .build());

    mockMvc.perform(get("/messages"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Test User"));
  }
}