package dev.lilianagorga.mywebsite;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MyWebsiteApplicationTests extends AbstractTestConfig {

  @Test
  void contextLoads() {
  }

}
