package dev.lilianagorga.mywebsite;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyWebsiteApplication {

  public static void main(String[] args) {

    String activeProfile = System.getProperty("spring.profiles.active", "dev");
    String dotenvFile = switch (activeProfile) {
      case "prod" -> ".env.production";
      case "testing" -> ".env.testing";
      default -> ".env";
    };
    Dotenv dotenv = Dotenv.configure().filename(dotenvFile).load();

    if ("prod".equals(activeProfile)) {
      System.setProperty("spring.datasource.url", dotenv.get("PROD_DB_URL"));
      System.setProperty("spring.datasource.username", dotenv.get("PROD_DB_USERNAME"));
      System.setProperty("spring.datasource.password", dotenv.get("PROD_DB_PASSWORD"));
    } else if ("testing".equals(activeProfile)) {
      System.setProperty("spring.datasource.url", dotenv.get("TEST_DB_URL"));
      System.setProperty("spring.datasource.username", dotenv.get("TEST_DB_USERNAME"));
      System.setProperty("spring.datasource.password", dotenv.get("TEST_DB_PASSWORD"));
    } else {
      System.setProperty("spring.datasource.url", dotenv.get("DB_URL"));
      System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME"));
      System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));
    }

    SpringApplication.run(MyWebsiteApplication.class, args);
  }

}
