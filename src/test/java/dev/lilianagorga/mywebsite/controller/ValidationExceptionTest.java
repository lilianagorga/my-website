package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import dev.lilianagorga.mywebsite.repository.MessageRepository;
import dev.lilianagorga.mywebsite.repository.ProjectRepository;
import dev.lilianagorga.mywebsite.repository.UserRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class ValidationExceptionTest extends AbstractTestConfig {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ProjectRepository projectRepository;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
    messageRepository.deleteAll();
    projectRepository.deleteAll();
  }

  @Test
  void shouldReturnValidationErrorForInvalidUser() throws Exception {
    String invalidUserJson = """
                {
                    "username": "",
                    "email": "invalid-email",
                    "password": "short"
                }
            """;

    mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidUserJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.statusCode").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.details.username").value("Username is mandatory"))
            .andExpect(jsonPath("$.details.email").value("Invalid email format"))
            .andExpect(jsonPath("$.details.password").value("Password must be between 8 and 20 characters"));
  }

  @Test
  void shouldReturnValidationErrorForInvalidMessage() throws Exception {
    String invalidMessageJson = """
            {
                "name": "",
                "email": "invalid-email",
                "message": ""
            }
        """;

    mockMvc.perform(post("/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidMessageJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.statusCode").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.details.name").value("Name is mandatory"))
            .andExpect(jsonPath("$.details.email").value("Invalid email format"))
            .andExpect(jsonPath("$.details.message").value("Message cannot be empty"));
  }

  @Test
  void shouldReturnValidationErrorForInvalidProject() throws Exception {
    String invalidProjectJson = """
            {
                "title": "",
                "description": "",
                "image_url": "invalid-url",
                "github_url": "invalid-url",
                "demo_url": "invalid-url",
                "tech_stack": []
            }
        """;

    mockMvc.perform(post("/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidProjectJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.statusCode").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.details.imageUrl").value("Image URL is mandatory"))
            .andExpect(jsonPath("$.details.githubUrl").value("GitHub URL is mandatory"))
            .andExpect(jsonPath("$.details.demoUrl").value("Demo URL is mandatory"))
            .andExpect(jsonPath("$.details.description").value("Description is mandatory"))
            .andExpect(jsonPath("$.details.title").value("Title is mandatory"));
  }
}