package dev.lilianagorga.mywebsite.seeder;

import dev.lilianagorga.mywebsite.entity.Project;
import dev.lilianagorga.mywebsite.entity.User;
import dev.lilianagorga.mywebsite.entity.Message;
import dev.lilianagorga.mywebsite.repository.ProjectRepository;
import dev.lilianagorga.mywebsite.repository.UserRepository;
import dev.lilianagorga.mywebsite.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Component
@Profile("dev")
public class DatabaseSeeder {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
  private final UserRepository userRepository;
  private final ProjectRepository projectRepository;
  private final MessageRepository messageRepository;
  private final PasswordEncoder passwordEncoder;

  public DatabaseSeeder(UserRepository userRepository, ProjectRepository projectRepository, MessageRepository messageRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.projectRepository = projectRepository;
    this.messageRepository = messageRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostConstruct
  public void seedDatabase() {
    userRepository.deleteAll();
    projectRepository.deleteAll();
    messageRepository.deleteAll();
    seedUsers();
    seedProjects();
    seedMessages();
    logger.info("Development database populated successfully!");
    logger.info("Users in database after seeding:");
    userRepository.findAll().forEach(user -> logger.info("User: {}, Roles: {}", user.getEmail(), user.getRoles()));
  }

  private void seedUsers() {
    Optional<User> admin = userRepository.findByEmail("admin@email.com");
    if (admin.isEmpty()) {
      userRepository.save(
              User.builder()
                      .username("admin")
                      .email("admin@email.com")
                      .password(passwordEncoder.encode("securePassword123"))
                      .roles(List.of("ADMIN"))
                      .build()
      );
    }

    Optional<User> regularUser = userRepository.findByEmail("liliana@email.com");
    if (regularUser.isEmpty()) {
      userRepository.save(
              User.builder()
                      .username("liliana")
                      .email("liliana@email.com")
                      .password(passwordEncoder.encode("securePassword123"))
                      .roles(List.of("USER"))
                      .build()
      );
    }
  }

  private void seedProjects() {
    Optional<Project> portfolioProject = projectRepository.findByTitle("Personal Portfolio");
    if (portfolioProject.isEmpty()) {
      projectRepository.save(
              Project.builder()
                      .title("Personal Portfolio")
                      .description("A personal portfolio to showcase projects and skills.")
                      .imageUrl("https://example.com/portfolio.jpg")
                      .techStack(List.of("Java", "Spring Boot", "MongoDB"))
                      .githubUrl("https://github.com/username/portfolio")
                      .demoUrl("https://username.github.io/portfolio")
                      .build()
      );
    }

    Optional<Project> ecommerceProject = projectRepository.findByTitle("E-Commerce App");
    if (ecommerceProject.isEmpty()) {
      projectRepository.save(
              Project.builder()
                      .title("E-Commerce App")
                      .description("An e-commerce application with a modern frontend and robust backend.")
                      .imageUrl("https://example.com/ecommerce.jpg")
                      .techStack(List.of("React", "Node.js", "MongoDB"))
                      .githubUrl("https://github.com/username/ecommerce-app")
                      .demoUrl("https://ecommerce.example.com")
                      .build()
      );
    }
  }

  private void seedMessages() {
    Optional<Message> message1 = messageRepository.findByEmail("alice@example.com");
    if (message1.isEmpty()) {
      messageRepository.save(
              Message.builder()
                      .name("Alice")
                      .email("alice@example.com")
                      .message("Hello, I love your portfolio!")
                      .build()
      );
    }

    Optional<Message> message2 = messageRepository.findByEmail("bob@example.com");
    if (message2.isEmpty()) {
      messageRepository.save(
              Message.builder()
                      .name("Bob")
                      .email("bob@example.com")
                      .message("Can you help me with a project?")
                      .build()
      );
    }
  }
}