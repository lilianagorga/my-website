package dev.lilianagorga.mywebsite;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@ActiveProfiles("test")
public class AbstractTestConfig {

  @DynamicPropertySource
  static void configureDynamicProperties(DynamicPropertyRegistry registry) {
    Dotenv dotenv = Dotenv.configure().filename(".env.testing").load();
    registry.add("spring.data.mongodb.uri", () -> dotenv.get("TEST_DB_URL"));
    registry.add("jwt.secret", () -> dotenv.get("JWT_SECRET"));
    registry.add("jwt.expirationMs", () -> dotenv.get("JWT_EXPIRATION_MS"));
    registry.add("sendgrid.api.key", () -> dotenv.get("MOCK_KEY"));
    registry.add("site.owner.email", () -> dotenv.get("MOCK_EMAIL"));
    registry.add("admin.email", () -> dotenv.get("ADMIN_MOCK_EMAIL"));
  }
}
