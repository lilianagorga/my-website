package dev.lilianagorga.mywebsite.repository;

import dev.lilianagorga.mywebsite.entity.User;
import dev.lilianagorga.mywebsite.AbstractTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest extends AbstractTestConfig {

  @Autowired
  private UserRepository userRepository;

  private User testUser;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();

    testUser = User.builder()
            .username("testUser")
            .email("test@example.com")
            .password("password123")
            .build();

    userRepository.save(testUser);
  }

  @AfterEach
  void cleanup() {
    userRepository.deleteAll();
  }

  @Test
  void testFindByEmail() {
    Optional<User> retrievedUser = userRepository.findByEmail("test@example.com");

    assertThat(retrievedUser).isPresent();
    assertThat(retrievedUser.get().getUsername()).isEqualTo("testUser");
  }

  @Test
  void testDeleteUser() {
    userRepository.deleteById(testUser.getId());
    Optional<User> deletedUser = userRepository.findByEmail("test@example.com");

    assertThat(deletedUser).isNotPresent();
  }
}