package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import dev.lilianagorga.mywebsite.entity.Message;
import dev.lilianagorga.mywebsite.repository.MessageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class MessageControllerTest extends AbstractTestConfig {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private MessageRepository messageRepository;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
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