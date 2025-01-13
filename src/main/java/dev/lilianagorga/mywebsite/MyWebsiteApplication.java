package dev.lilianagorga.mywebsite;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Objects;

@SpringBootApplication
public class MyWebsiteApplication {

  public static void main(String[] args) {

    String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
    if (activeProfile == null) {
      activeProfile = "dev";
    }
    System.setProperty("spring.profiles.active", activeProfile);
    if (!"prod".equals(activeProfile)) {
      String envFileName = activeProfile.equals("dev") ? ".env" : ".env." + activeProfile;
      Dotenv dotenv = Dotenv.configure().filename(envFileName).load();

      String dbUriKey = "test".equals(activeProfile) ? "TEST_DB_URL" : "DB_URL";

      String dbUri = dotenv.get(dbUriKey);
      if (dbUri == null) {
        throw new IllegalStateException("Variable " + dbUriKey + " not found in file " + envFileName);
      }
      System.setProperty("spring.data.mongodb.uri", dbUri);
      String jwtSecret = dotenv.get("JWT_SECRET");
      if (jwtSecret == null) {
        throw new IllegalStateException("Variable JWT_SECRET not found in file " + envFileName);
      }
      System.setProperty("jwt.secret", jwtSecret);
      String jwtExpirationMs = dotenv.get("JWT_EXPIRATION_MS");
      System.setProperty("jwt.expirationMs", Objects.requireNonNullElse(jwtExpirationMs, "86400000"));

      String sendGridApiKey = dotenv.get("SENDGRID_API_KEY");
      if (sendGridApiKey == null) {
        throw new IllegalStateException("Variable SENDGRID_API_KEY not found in file " + envFileName);
      }
      System.setProperty("sendgrid.api.Key", sendGridApiKey);
    }

    SpringApplication.run(MyWebsiteApplication.class, args);
  }

}
