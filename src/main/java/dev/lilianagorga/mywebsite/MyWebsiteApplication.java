package dev.lilianagorga.mywebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MyWebsiteApplication {

  public static void main(String[] args) {
    String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
    if (activeProfile == null) {
      activeProfile = "dev";
    }
    System.setProperty("spring.profiles.active", activeProfile);

    SpringApplication.run(MyWebsiteApplication.class, args);
  }

}