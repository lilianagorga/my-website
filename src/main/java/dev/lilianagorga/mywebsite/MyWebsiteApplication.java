package dev.lilianagorga.mywebsite;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyWebsiteApplication {

  public static void main(String[] args) {

    String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
    if (activeProfile == null) {
      activeProfile = "dev";
    }
    System.setProperty("spring.profiles.active", activeProfile);
    String envFileName = activeProfile.equals("dev") ? ".env" : ".env." + activeProfile;
    Dotenv dotenv = Dotenv.configure().filename(envFileName).load();

    String dbUriKey = switch (activeProfile) {
      case "test" -> "TEST_DB_URL";
      case "prod" -> "PROD_DB_URL";
      default -> "DB_URL";
    };

    String dbUri = dotenv.get(dbUriKey);
    if (dbUri == null) {
      throw new IllegalStateException("Variable " + dbUriKey + " not found into file " + envFileName);
    }
    System.setProperty("spring.data.mongodb.uri", dbUri);

    SpringApplication.run(MyWebsiteApplication.class, args);
  }

}
