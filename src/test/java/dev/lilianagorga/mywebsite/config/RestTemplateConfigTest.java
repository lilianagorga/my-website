package dev.lilianagorga.mywebsite.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dev.lilianagorga.mywebsite.AbstractTestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {TestRestTemplateConfig.class})
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
class RestTemplateConfigTest extends AbstractTestConfig {

  private static WireMockServer wireMockServer;

  @Autowired
  private RestTemplate restTemplate;

  @BeforeAll
  static void setUp() {
    wireMockServer = new WireMockServer(WireMockConfiguration.options()
            .dynamicPort()
            .disableRequestJournal().asynchronousResponseEnabled(true));
    wireMockServer.start();
    WireMock.configureFor("localhost", wireMockServer.port());
  }

  @AfterAll
  static void tearDown() {
    if (wireMockServer != null && wireMockServer.isRunning()) {
      wireMockServer.stop();
    }
  }

  @Test
  void restTemplateShouldNotBeNull() {
    assertNotNull(restTemplate, "RestTemplate should not be null");
  }

  @Test
  void wireMockServerShouldStartProperly() {
    assertTrue(wireMockServer.isRunning(), "WireMockServer should be running");
  }

  @Test
  void restTemplateShouldSendAuthorizedRequest() {
    wireMockServer.stubFor(get(urlEqualTo("/test"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withBody("Authorized")));
    String url = "http://localhost:" + wireMockServer.port() + "/test";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    assertEquals(200, response.getStatusCode().value(), "Status code should be 200");
    assertEquals("Authorized", response.getBody(), "Response body should be Authorized");
  }
}