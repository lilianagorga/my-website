package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.entity.Project;
import dev.lilianagorga.mywebsite.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  private final ProjectService projectService;

  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Project createProject(@Valid @RequestBody Project project) {
    return projectService.createProject(project);
  }

  @GetMapping("/{id}")
  public Optional<Project> getProjectById(@PathVariable String id) {
    return projectService.getProjectById(id);
  }

  @GetMapping
  public List<Project> getAllProjects() {
    return projectService.getAllProjects();
  }

  @PutMapping("/{id}")
  public Project updateProject(@PathVariable String id, @Valid @RequestBody Project updatedProject) {
    return projectService.updateProject(id, updatedProject);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProject(@PathVariable String id) {
    projectService.deleteProject(id);
  }
}