package dev.lilianagorga.mywebsite.seeder;

import dev.lilianagorga.mywebsite.entities.Project;
import dev.lilianagorga.mywebsite.entities.User;
import dev.lilianagorga.mywebsite.entities.Message;
import dev.lilianagorga.mywebsite.repositories.ProjectRepository;
import dev.lilianagorga.mywebsite.repositories.UserRepository;
import dev.lilianagorga.mywebsite.repositories.MessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
@Profile("dev")
public class DatabaseSeeder {

  private final UserRepository userRepository;
  private final ProjectRepository projectRepository;
  private final MessageRepository messageRepository;

  public DatabaseSeeder(UserRepository userRepository, ProjectRepository projectRepository, MessageRepository messageRepository) {
    this.userRepository = userRepository;
    this.projectRepository = projectRepository;
    this.messageRepository = messageRepository;
  }

  @PostConstruct
  public void seedDatabase() {
    userRepository.deleteAll();
    projectRepository.deleteAll();
    messageRepository.deleteAll();

    User admin = User.builder()
            .username("admin")
            .email("admin@example.com")
            .password("securePassword123")
            .roles(List.of("ADMIN"))
            .build();

    User regularUser = User.builder()
            .username("john_doe")
            .email("johndoe@example.com")
            .password("password123")
            .roles(List.of("USER"))
            .build();

    userRepository.save(admin);
    userRepository.save(regularUser);

    Project project1 = Project.builder()
            .title("Personal Portfolio")
            .description("A personal portfolio to showcase projects and skills.")
            .imageUrl("https://example.com/portfolio.jpg")
            .techStack(List.of("Java", "Spring Boot", "MongoDB"))
            .githubUrl("https://github.com/username/portfolio")
            .demoUrl("https://username.github.io/portfolio")
            .build();

    Project project2 = Project.builder()
            .title("E-Commerce App")
            .description("An e-commerce application with a modern frontend and robust backend.")
            .imageUrl("https://example.com/ecommerce.jpg")
            .techStack(List.of("React", "Node.js", "MongoDB"))
            .githubUrl("https://github.com/username/ecommerce-app")
            .demoUrl("https://ecommerce.example.com")
            .build();

    projectRepository.save(project1);
    projectRepository.save(project2);
    Message message1 = Message.builder()
            .name("Alice")
            .email("alice@example.com")
            .message("Hello, I love your portfolio!")
            .build();

    Message message2 = Message.builder()
            .name("Bob")
            .email("bob@example.com")
            .message("Can you help me with a project?")
            .build();

    messageRepository.save(message1);
    messageRepository.save(message2);

    System.out.println("Development database populated successfully!");
  }
}