package dev.lilianagorga.mywebsite.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
  private static final int MAX_RETRIES = 3;
  private static final long INITIAL_BACKOFF_MS = 1000;
  private static final long MAX_BACKOFF_MS = 8000;

  public MongoDBIpUpdater(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @PostConstruct
  public void logConfig() {
    log.info("MongoDBIpUpdater Config - Public Key: {}", publicKey);
    log.info("MongoDBIpUpdater Config - Private Key: {}", privateKey != null ? "***" : "null");
    log.info("MongoDBIpUpdater Config - Project ID: {}", projectId);
  }

  public boolean updateIpAddress() {
    int attempt = 0;
    long backoffMs = INITIAL_BACKOFF_MS;

    while (attempt < MAX_RETRIES) {
      try {
        attempt++;
        log.info("IP update attempt {}/{}", attempt, MAX_RETRIES);

        if (publicKey == null || privateKey == null || projectId == null) {
          log.error("MongoDB Atlas credentials are not configured. Check environment variables.");
          return false;
        }

        String ipAddress = getPublicIp();
        log.info("Current public IP: {}", ipAddress);

        if (isIpAlreadyWhitelisted(ipAddress)) {
          log.info("IP {} is already whitelisted in MongoDB Atlas", ipAddress);
          return true;
        }

        boolean added = addIpToWhitelist(ipAddress);
        if (added) {
          log.info("Successfully added IP {} to MongoDB Atlas whitelist", ipAddress);
          return true;
        }

        log.warn("Failed to add IP to whitelist on attempt {}", attempt);

      } catch (HttpClientErrorException | HttpServerErrorException e) {
        log.error("HTTP error during IP update (attempt {}): {} - {}",
                  attempt, e.getStatusCode(), e.getResponseBodyAsString());

        if (e.getStatusCode().is4xxClientError() &&
            e.getStatusCode().value() != 429) {
          log.error("Client error - not retrying");
          return false;
        }
      } catch (Exception e) {
        log.error("Error during IP update (attempt {}): {}", attempt, e.getMessage());
      }

      if (attempt < MAX_RETRIES) {
        try {
          log.info("Waiting {} ms before retry...", backoffMs);
          Thread.sleep(backoffMs);
          backoffMs = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          return false;
        }
      }
    }

    log.error("Failed to update IP after {} attempts", MAX_RETRIES);
    return false;
  }

  private boolean isIpAlreadyWhitelisted(String ipAddress) {
    try {
      String listUrl = BASE_URL + "/groups/" + projectId + "/accessList?itemsPerPage=500";
      ResponseEntity<String> response = restTemplate.getForEntity(listUrl, String.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode results = mapper.readTree(response.getBody()).get("results");

        if (results != null && results.isArray()) {
          for (JsonNode entry : results) {
            JsonNode ipNode = entry.get("ipAddress");
            if (ipNode != null && ipNode.asText().equals(ipAddress)) {
              return true;
            }
          }
        }
      }
    } catch (Exception e) {
      log.warn("Error checking if IP is whitelisted: {}", e.getMessage());
    }
    return false;
  }

  private boolean addIpToWhitelist(String ipAddress) {
    try {
      String url = BASE_URL + "/groups/" + projectId + "/accessList";
      String payload = "[{\"ipAddress\": \"" + ipAddress + "\", \"comment\": \"Auto-added by app\"}]";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      HttpEntity<String> request = new HttpEntity<>(payload, headers);

      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

      log.info("MongoDB Atlas API response: {}", response.getStatusCode());

      return response.getStatusCode().is2xxSuccessful();
    } catch (Exception e) {
      log.error("Error adding IP to whitelist: {}", e.getMessage());
      throw e;
    }
  }

  protected BufferedReader createBufferedReader(URL url) throws IOException {
    return new BufferedReader(new InputStreamReader(url.openStream()));
  }

  public String getPublicIp() throws IOException {
    String[] ipServices = {
      "https://api.ipify.org?format=json",
      "https://api64.ipify.org?format=json",
      "https://ifconfig.me/ip"
    };

    for (String serviceUrl : ipServices) {
      try {
        URI uri = new URI(serviceUrl);
        URL url = uri.toURL();
        BufferedReader in = createBufferedReader(url);
        String response = in.readLine();
        in.close();

        if (serviceUrl.contains("json")) {
          ObjectMapper mapper = new ObjectMapper();
          return mapper.readTree(response).get("ip").asText();
        } else {
          return response.trim();
        }
      } catch (Exception e) {
        log.warn("Failed to get IP from {}: {}", serviceUrl, e.getMessage());
      }
    }

    throw new IOException("Failed to fetch public IP from all services");
  }
}
