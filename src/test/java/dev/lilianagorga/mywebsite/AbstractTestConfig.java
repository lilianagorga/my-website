package dev.lilianagorga.mywebsite;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@ActiveProfiles("testing")
public abstract class AbstractTestConfig {

  @DynamicPropertySource
  static void configureDynamicProperties(DynamicPropertyRegistry registry) {
    Dotenv dotenv = Dotenv.configure().filename(".env.testing").load();
    registry.add("spring.datasource.url", () -> dotenv.get("TEST_DB_URL"));
    registry.add("spring.datasource.username", () -> dotenv.get("TEST_DB_USERNAME"));
    registry.add("spring.datasource.password", () -> dotenv.get("TEST_DB_PASSWORD"));
  }
}
