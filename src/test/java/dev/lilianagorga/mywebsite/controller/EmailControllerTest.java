package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.service.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@ActiveProfiles("test")
class EmailControllerTest {

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
  void shouldMockEmailServiceAndReturnResponse() throws Exception {
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new EmailController(emailService)).build();

    Mockito.when(emailService.sendEmail("test@example.com", "Test Subject", "Test Body"))
            .thenReturn("Email not sent (Mocked). Active profile: test");

    String emailJson = """
                {
                    "to": "test@example.com",
                    "subject": "Test Subject",
                    "body": "Test Body"
                }
            """;
    mockMvc.perform(post("/send-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(emailJson))
            .andExpect(status().isOk())
            .andExpect(content().string("Email not sent (Mocked). Active profile: test"));
  }
}