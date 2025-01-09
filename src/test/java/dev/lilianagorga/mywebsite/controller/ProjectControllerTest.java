package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import dev.lilianagorga.mywebsite.entity.Project;
import dev.lilianagorga.mywebsite.repository.ProjectRepository;
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
class ProjectControllerTest extends AbstractTestConfig {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ProjectRepository projectRepository;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @AfterEach
  void tearDown() {
    projectRepository.deleteAll();
  }

  @Test
  void shouldCreateProject() throws Exception {
    String projectJson = """
                {
                    "title": "Test Project",
                    "description": "A test project description",
                    "imageUrl": "https://example.com/image.jpg",
                    "githubUrl": "https://github.com/test/project",
                    "demoUrl": "https://example.com/demo",
                    "techStack": ["Java", "Spring Boot"]
                }
            """;

    mockMvc.perform(post("/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(projectJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Test Project"))
            .andExpect(jsonPath("$.description").value("A test project description"));
  }

  @Test
  void shouldGetAllProjects() throws Exception {
    projectRepository.save(Project.builder()
            .title("Test Project")
            .description("A test project description")
            .imageUrl("https://example.com/image.jpg")
            .githubUrl("https://github.com/test/project")
            .demoUrl("https://example.com/demo")
            .build());

    mockMvc.perform(get("/projects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Test Project"));
  }
}