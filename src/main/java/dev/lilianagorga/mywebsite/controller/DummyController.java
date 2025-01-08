package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.entity.Message;
import dev.lilianagorga.mywebsite.entity.Project;
import dev.lilianagorga.mywebsite.entity.User;
import dev.lilianagorga.mywebsite.repository.MessageRepository;
import dev.lilianagorga.mywebsite.repository.ProjectRepository;
import dev.lilianagorga.mywebsite.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class DummyController {

  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ProjectRepository projectRepository;

  public DummyController(UserRepository userRepository, MessageRepository messageRepository, ProjectRepository projectRepository) {
    this.userRepository = userRepository;
    this.messageRepository = messageRepository;
    this.projectRepository = projectRepository;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  public User createUser(@Valid @RequestBody User user) {
    return userRepository.save(user);
  }

  @PostMapping("/messages")
  @ResponseStatus(HttpStatus.CREATED)
  public Message createMessage(@Valid @RequestBody Message message) {
    return messageRepository.save(message);
  }

  @PostMapping("/projects")
  @ResponseStatus(HttpStatus.CREATED)
  public Project createProject(@Valid @RequestBody Project project) {
    return projectRepository.save(project);
  }
}