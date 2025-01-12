package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import dev.lilianagorga.mywebsite.config.SecurityConfig;
import dev.lilianagorga.mywebsite.entity.User;
import dev.lilianagorga.mywebsite.repository.UserRepository;
import dev.lilianagorga.mywebsite.request.LoginRequest;
import dev.lilianagorga.mywebsite.response.AuthResponse;
import dev.lilianagorga.mywebsite.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(SecurityConfig.class)
class AuthControllerTest extends AbstractTestConfig {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthController authController;

  @BeforeEach
  void cleanDatabaseBeforeEach() {
    userRepository.deleteAll();
  }

  @AfterEach
  void cleanDatabaseAfterEach() {
    userRepository.deleteAll();
  }

  @Test
  void testRegisterSuccess() {
    User user = new User();
    user.setUsername("testUser");
    user.setEmail("test@example.com");
    user.setPassword("password");

    AuthResponse response = authController.register(user);

    assertEquals("User registered successfully.", response.getMessage());
    User savedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
    assertEquals(user.getEmail(), savedUser.getEmail());
    assertTrue(passwordEncoder.matches("password", savedUser.getPassword()));
  }

  @Test
  void testRegisterUserAlreadyExists() {
    User existingUser = new User();
    existingUser.setUsername("existingUser");
    existingUser.setEmail("test@example.com");
    existingUser.setPassword(passwordEncoder.encode("password"));
    userRepository.save(existingUser);

    User newUser = new User();
    newUser.setUsername("newUser");
    newUser.setEmail("test@example.com");
    newUser.setPassword("password");

    AuthResponse response = authController.register(newUser);

    assertEquals("Email already in use.", response.getMessage());
  }

  @Test
  void testLoginSuccess() {
    User existingUser = new User();
    existingUser.setUsername("testUser");
    existingUser.setEmail("test@example.com");
    existingUser.setPassword(passwordEncoder.encode("password"));
    userRepository.save(existingUser);

    LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
    AuthResponse response = authController.login(loginRequest);

    assertEquals("Login successful.", response.getMessage());
    assertNotNull(response.getToken());

    String usernameFromToken = jwtUtil.getEmailFromToken(response.getToken());
    assertEquals(existingUser.getEmail(), usernameFromToken);
  }

  @Test
  void testLoginInvalidCredentials() {
    LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongPassword");

    Exception exception = assertThrows(RuntimeException.class, () -> {
      authController.login(loginRequest);
    });

    assertEquals("Bad credentials", exception.getMessage());
  }

  @Test
  void testJwtTokenGeneration() {
    String email = "test@example.com";
    List<String> roles = List.of("USER", "ADMIN");
    String token = jwtUtil.generateToken(email, roles);
    assertNotNull(token);
    try {
      String usernameFromToken = jwtUtil.getEmailFromToken(token);

      assertEquals(email, usernameFromToken);
      assertTrue(jwtUtil.validateToken(token));
    } catch (Exception e) {
      System.out.println("Error during token validation: " + e.getMessage());
      fail("Token parsing failed: " + e.getMessage());
    }
  }

  @Test
  void testLogout() {
    AuthResponse response = authController.logout();
    assertEquals("Logout successful.", response.getMessage());
    assertNull(response.getToken());
  }
}