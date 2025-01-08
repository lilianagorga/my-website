package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.entity.Project;
import dev.lilianagorga.mywebsite.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

  private final ProjectRepository projectRepository;

  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public Project createProject(Project project) {
    return projectRepository.save(project);
  }

  public Optional<Project> getProjectById(String id) {
    return projectRepository.findById(id);
  }

  public List<Project> getAllProjects() {
    return projectRepository.findAll();
  }

  public Optional<Project> getProjectByTitle(String title) {
    return projectRepository.findByTitle(title);
  }

  public Project updateProject(String id, Project updatedProject) {
    return projectRepository.findById(id).map(existingProject -> {
      existingProject.setTitle(updatedProject.getTitle());
      existingProject.setDescription(updatedProject.getDescription());
      existingProject.setImageUrl(updatedProject.getImageUrl());
      existingProject.setTechStack(updatedProject.getTechStack());
      existingProject.setGithubUrl(updatedProject.getGithubUrl());
      existingProject.setDemoUrl(updatedProject.getDemoUrl());
      return projectRepository.save(existingProject);
    }).orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
  }

  public void deleteProject(String id) {
    if (!projectRepository.existsById(id)) {
      throw new RuntimeException("Project not found with id: " + id);
    }
    projectRepository.deleteById(id);
  }
}