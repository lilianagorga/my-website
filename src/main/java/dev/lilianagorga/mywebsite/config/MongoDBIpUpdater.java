package dev.lilianagorga.mywebsite.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Collections;

@Component
@Slf4j
@Data
public class MongoDBIpUpdater {

  private RestTemplate restTemplate;

  @Value("${mongo.project.id}")
  private String projectId;

  @Value("${mongo.api.public.key}")
  private String publicKey;

  @Value("${mongo.api.private.key}")
  private String privateKey;

  private static final String BASE_URL = "https://cloud.mongodb.com/api/atlas/v1.0";

  public MongoDBIpUpdater(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @PostConstruct
  public void logConfig() {
    log.info("MongoDBIpUpdater Config - Public Key: {}", publicKey);
    log.info("MongoDBIpUpdater Config - Private Key: {}", privateKey);
    log.info("MongoDBIpUpdater Config - Project ID: {}", projectId);
  }

  public void updateIpAddress() {
    try {
      if (publicKey == null || privateKey == null || projectId == null) {
        log.error("Environment variables are not set correctly.");
        return;
      }

      String ipAddress = getPublicIp().trim();
      String url = BASE_URL + "/groups/" + projectId + "/accessList";
      String payload = "[{\"ipAddress\": \"" + ipAddress + "\", \"comment\": \"Auto-added by app\"}]";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      HttpEntity<String> request = new HttpEntity<>(payload, headers);

      String listUrl = BASE_URL + "/groups/" + projectId + "/accessList?itemsPerPage=500";
      ResponseEntity<String> existing = restTemplate.getForEntity(listUrl, String.class);

      if (existing.getStatusCode().is2xxSuccessful() && existing.getBody() != null) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode results = mapper.readTree(existing.getBody()).get("results");
        boolean alreadyExists = false;
        for (JsonNode entry : results) {
          if (entry.get("ipAddress").asText().equals(ipAddress)) {
            alreadyExists = true;
            break;
          }
        }

        if (alreadyExists) {
          log.info("IP {} già presente nella Access List, nessuna azione necessaria.", ipAddress);
          return;
        }
      }

      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
      log.info("Response: {}", response.getStatusCode());
      log.info("Response Body: {}", response.getBody());

    } catch (Exception e) {
      log.error("Failed to update IP", e);
    }
  }

  protected BufferedReader createBufferedReader(URL url) throws IOException {
    return new BufferedReader(new InputStreamReader(url.openStream()));
  }

  protected String getPublicIp() throws IOException {
    try {
      URI uri = new URI("https://api.ipify.org?format=json");
      URL url = uri.toURL();
      BufferedReader in = createBufferedReader(url);
      String response = in.readLine();
      in.close();

      ObjectMapper mapper = new ObjectMapper();
      return mapper.readTree(response).get("ip").asText();
    } catch (Exception e) {
      throw new IOException("Failed to fetch public IP", e);
    }
  }
}