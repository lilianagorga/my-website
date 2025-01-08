package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import dev.lilianagorga.mywebsite.entity.Project;
import dev.lilianagorga.mywebsite.repository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest extends AbstractTestConfig {

  @Autowired
  private ProjectService projectService;

  @Autowired
  private ProjectRepository projectRepository;

  private Project testProject;

  @BeforeEach
  void setUp() {
    projectRepository.deleteAll();
    testProject = Project.builder()
            .title("Test Project")
            .description("Test Description")
            .imageUrl("https://example.com/test.jpg")
            .techStack(List.of("Java", "Spring Boot"))
            .githubUrl("https://github.com/test")
            .demoUrl("https://test.com/demo")
            .build();
    projectRepository.save(testProject);
  }

  @AfterEach
  void tearDown() {
    projectRepository.deleteAll();
  }

  @Test
  void createProject() {
    Project newProject = Project.builder()
            .title("New Project")
            .description("New Description")
            .imageUrl("https://example.com/new.jpg")
            .techStack(List.of("React", "Node.js"))
            .githubUrl("https://github.com/new")
            .demoUrl("https://new.com/demo")
            .build();
    Project createdProject = projectService.createProject(newProject);

    assertNotNull(createdProject.getId());
    assertEquals("New Project", createdProject.getTitle());
  }

  @Test
  void getProjectById() {
    Optional<Project> project = projectService.getProjectById(testProject.getId());
    assertTrue(project.isPresent());
    assertEquals("Test Project", project.get().getTitle());
  }

  @Test
  void getAllProjects() {
    List<Project> projects = projectService.getAllProjects();
    assertEquals(1, projects.size());
  }

  @Test
  void updateProject() {
    testProject.setTitle("Updated Project");
    Project updatedProject = projectService.updateProject(testProject.getId(), testProject);
    assertEquals("Updated Project", updatedProject.getTitle());
  }

  @Test
  void getProjectByTitle() {
    Optional<Project> project = projectService.getProjectByTitle("Test Project");
    assertTrue(project.isPresent());
    assertEquals("Test Project", project.get().getTitle());
  }

  @Test
  void deleteProject() {
    projectService.deleteProject(testProject.getId());
    Optional<Project> project = projectRepository.findById(testProject.getId());
    assertTrue(project.isEmpty());
  }
}