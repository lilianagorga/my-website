package dev.lilianagorga.mywebsite.config;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
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
  private static final String LIST_URL = BASE_URL + "?itemsPerPage=500";

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
    // arrange

    // act
    mongoDBIpUpdater.logConfig();

    // assert
    assertEquals("testPublicKey", mongoDBIpUpdater.getPublicKey());
    assertEquals("testPrivateKey", mongoDBIpUpdater.getPrivateKey());
    assertEquals("testProjectId", mongoDBIpUpdater.getProjectId());
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateIpAddress_shouldUpdateIpSuccessfully() throws Exception {
    // arrange
    doReturn("192.168.1.1").when(mongoDBIpUpdater).getPublicIp();
    when(restTemplateMock.getForEntity(eq(LIST_URL), eq(String.class)))
            .thenReturn(new ResponseEntity<>("{\"results\":[]}", HttpStatus.OK));
    when(restTemplateMock.postForEntity(eq(BASE_URL), any(HttpEntity.class), eq(String.class)))
            .thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

    // act
    mongoDBIpUpdater.updateIpAddress();

    // assert
    ArgumentCaptor<HttpEntity<String>> captor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(restTemplateMock).postForEntity(eq(BASE_URL), captor.capture(), eq(String.class));
    HttpEntity<String> captured = captor.getValue();
    assertNotNull(captured);
    assertEquals(MediaType.APPLICATION_JSON, captured.getHeaders().getContentType());
    assertTrue(captured.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON));
    String body = captured.getBody();
    assertNotNull(body);
    assertTrue(body.contains("\"ipAddress\""));
    assertTrue(body.contains("192.168.1.1"));
  }

  @Test
  void updateIpAddress_shouldHandleNon2xxResponse() throws Exception {
    // arrange
    doReturn("192.168.1.1").when(mongoDBIpUpdater).getPublicIp();
    when(restTemplateMock.getForEntity(eq(LIST_URL), eq(String.class)))
            .thenReturn(new ResponseEntity<>("{\"results\":[]}", HttpStatus.OK));
    when(restTemplateMock.postForEntity(eq(BASE_URL), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

    // act
    boolean result = mongoDBIpUpdater.updateIpAddress();

    // assert
    assertFalse(result);
    verify(restTemplateMock).postForEntity(eq(BASE_URL), any(HttpEntity.class), eq(String.class));
  }

  @Test
  void updateIpAddress_shouldSkipPostIfIpAlreadyExists() throws Exception {
    // arrange
    doReturn("192.168.1.1").when(mongoDBIpUpdater).getPublicIp();
    String existingJson = """
        {"results":[{"ipAddress":"192.168.1.1","comment":"any"}]}
        """;
    when(restTemplateMock.getForEntity(eq(LIST_URL), eq(String.class)))
            .thenReturn(new ResponseEntity<>(existingJson, HttpStatus.OK));

    // act
    mongoDBIpUpdater.updateIpAddress();

    // assert
    verify(restTemplateMock, never()).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
  }

  @Test
  void updateIpAddress_shouldLogErrorIfEnvironmentVariablesAreNotSet() {
    // arrange
    mongoDBIpUpdater.setProjectId(null);
    mongoDBIpUpdater.setPublicKey(null);
    mongoDBIpUpdater.setPrivateKey(null);

    // act
    mongoDBIpUpdater.updateIpAddress();

    // assert
    verify(restTemplateMock, never()).getForEntity(anyString(), eq(String.class));
    verify(restTemplateMock, never()).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
  }

  @Test
  void getPublicIp_shouldReturnIpAddress() throws Exception {
    // arrange
    String jsonResponse = "{\"ip\": \"192.168.1.1\"}";
    BufferedReader mockReader = Mockito.mock(BufferedReader.class);
    when(mockReader.readLine()).thenReturn(jsonResponse);
    doReturn(mockReader).when(mongoDBIpUpdater).createBufferedReader(any(URL.class));

    // act
    String publicIp = mongoDBIpUpdater.getPublicIp();

    // assert
    assertEquals("192.168.1.1", publicIp);
  }

  @Test
  void getPublicIp_shouldThrowIOExceptionOnFailure() throws Exception {
    // arrange
    doThrow(new IOException("Connection failed"))
            .when(mongoDBIpUpdater).createBufferedReader(any(URL.class));

    // act + assert
    assertThrows(IOException.class, mongoDBIpUpdater::getPublicIp);
  }
}