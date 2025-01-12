package dev.lilianagorga.mywebsite.service;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import dev.lilianagorga.mywebsite.entity.User;
import dev.lilianagorga.mywebsite.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UserServiceTest extends AbstractTestConfig {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;


  private User testUser;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    testUser = User.builder()
            .username("testUser")
            .email("testuser@example.com")
            .password("password123")
            .roles(List.of("USER"))
            .build();
    userRepository.save(testUser);
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  void createUser() {
    User newUser = User.builder()
            .username("newUser")
            .email("newuser@example.com")
            .password("newPassword123")
            .roles(List.of("USER"))
            .build();
    User createdUser = userService.createUser(newUser);

    assertNotNull(createdUser.getId());
    assertEquals("newUser", createdUser.getUsername());
  }

  @Test
  void getUserById() {
    Optional<User> user = userService.getUserById(testUser.getId());
    assertTrue(user.isPresent());
    assertEquals("testUser", user.get().getUsername());
  }

  @Test
  void getAllUsers() {
    List<User> users = userService.getAllUsers();
    assertEquals(1, users.size());
  }

  @Test
  void updateUser() {
    testUser.setUsername("updatedUser");
    User updatedUser = userService.updateUser(testUser.getId(), testUser);
    assertEquals("updatedUser", updatedUser.getUsername());
  }

  @Test
  void deleteUser() {
    userService.deleteUser(testUser.getId());
    Optional<User> user = userRepository.findById(testUser.getId());
    assertTrue(user.isEmpty());
  }
}