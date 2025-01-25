package dev.lilianagorga.mywebsite.config;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MongoDBIpUpdaterTest extends AbstractTestConfig {

  private MongoDBIpUpdater mongoDBIpUpdater;
  private RestTemplate restTemplateMock;

  private static final String BASE_URL = "https://cloud.mongodb.com/api/atlas/v1.0/groups/testProjectId/accessList";

  @BeforeEach
  void setUp() {
    restTemplateMock = Mockito.mock(RestTemplate.class);
    mongoDBIpUpdater = Mockito.spy(new MongoDBIpUpdater(restTemplateMock));
    mongoDBIpUpdater.setProjectId("testProjectId");
    mongoDBIpUpdater.setPublicKey("testPublicKey");
    mongoDBIpUpdater.setPrivateKey("testPrivateKey");
  }

  @Test
  void logConfig_shouldLogConfigurationValues() {
    mongoDBIpUpdater.logConfig();

    assertEquals("testPublicKey", mongoDBIpUpdater.getPublicKey());
    assertEquals("testPrivateKey", mongoDBIpUpdater.getPrivateKey());
    assertEquals("testProjectId", mongoDBIpUpdater.getProjectId());
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateIpAddress_shouldUpdateIpSuccessfully() throws Exception {
    doReturn("192.168.1.1").when(mongoDBIpUpdater).getPublicIp();

    ResponseEntity<String> mockResponse = new ResponseEntity<>("Success", HttpStatus.OK);
    when(restTemplateMock.postForEntity(eq(BASE_URL), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

    mongoDBIpUpdater.updateIpAddress();

    ArgumentCaptor<HttpEntity<String>> captor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(restTemplateMock).postForEntity(eq(BASE_URL), captor.capture(), eq(String.class));

    HttpEntity<String> capturedRequest = captor.getValue();
    assertNotNull(capturedRequest);
    HttpHeaders headers = capturedRequest.getHeaders();
    assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    assertTrue(headers.getAccept().contains(MediaType.APPLICATION_JSON));

    String expectedPayload = "[{\"ipAddress\": \"192.168.1.1\", \"comment\": \"Testing API\"}]";
    assertEquals(expectedPayload, capturedRequest.getBody());
  }

  @Test
  void updateIpAddress_shouldHandleNon2xxResponse() throws Exception {
    doReturn("192.168.1.1").when(mongoDBIpUpdater).getPublicIp();

    ResponseEntity<String> mockResponse = new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
    when(restTemplateMock.postForEntity(eq(BASE_URL), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

    mongoDBIpUpdater.updateIpAddress();

    verify(restTemplateMock).postForEntity(eq(BASE_URL), any(HttpEntity.class), eq(String.class));
  }

  @Test
  void updateIpAddress_shouldLogErrorIfEnvironmentVariablesAreNotSet() {
    mongoDBIpUpdater.setProjectId(null);
    mongoDBIpUpdater.setPublicKey(null);
    mongoDBIpUpdater.setPrivateKey(null);

    mongoDBIpUpdater.updateIpAddress();

    verify(restTemplateMock, never()).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
  }

  @Test
  void getPublicIp_shouldReturnIpAddress() throws Exception {
    String jsonResponse = "{\"ip\": \"192.168.1.1\"}";
    BufferedReader mockReader = Mockito.mock(BufferedReader.class);
    when(mockReader.readLine()).thenReturn(jsonResponse);

    doReturn(mockReader).when(mongoDBIpUpdater).createBufferedReader(any(URL.class));

    String publicIp = mongoDBIpUpdater.getPublicIp();
    assertEquals("192.168.1.1", publicIp);
  }

  @Test
  void getPublicIp_shouldThrowIOExceptionOnFailure() throws Exception {
    doThrow(new IOException("Connection failed"))
            .when(mongoDBIpUpdater).createBufferedReader(any(URL.class));

    assertThrows(IOException.class, mongoDBIpUpdater::getPublicIp);
  }
}