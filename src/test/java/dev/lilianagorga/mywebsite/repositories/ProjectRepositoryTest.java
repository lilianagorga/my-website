package dev.lilianagorga.mywebsite.repositories;

import dev.lilianagorga.mywebsite.entities.Project;
import dev.lilianagorga.mywebsite.AbstractTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ProjectRepositoryTest extends AbstractTestConfig {

  @Autowired
  private ProjectRepository projectRepository;

  private Project testProject;

  @BeforeEach
  void setUp() {
    projectRepository.deleteAll();

    testProject = Project.builder()
            .title("Test Project")
            .description("This is a test project")
            .imageUrl("https://example.com/image.jpg")
            .techStack(List.of("Java", "Spring Boot"))
            .githubUrl("https://github.com/example/test-project")
            .demoUrl("https://example.com/demo")
            .build();

    projectRepository.save(testProject);
  }

  @AfterEach
  void cleanup() {
    projectRepository.deleteAll();
  }

  @Test
  void testRetrieveProject() {
    List<Project> allProjects = projectRepository.findAll();
    assertThat(allProjects).hasSize(1);
    assertThat(allProjects.getFirst().getTitle()).isEqualTo("Test Project");
  }

  @Test
  void testDeleteProject() {
    // Act
    projectRepository.deleteById(testProject.getId());
    List<Project> allProjects = projectRepository.findAll();

    assertThat(allProjects).isEmpty();
  }
}