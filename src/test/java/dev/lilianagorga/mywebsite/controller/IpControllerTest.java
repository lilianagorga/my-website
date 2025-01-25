package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import dev.lilianagorga.mywebsite.config.MongoDBIpUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class IpControllerTest extends AbstractTestConfig {

  private IpController ipController;
  private MongoDBIpUpdater ipUpdaterMock;

  @BeforeEach
  void setUp() {
    ipUpdaterMock = Mockito.mock(MongoDBIpUpdater.class);
    ipController = new IpController(ipUpdaterMock);
  }

  @Test
  void updateIpManually_shouldInvokeIpUpdater() {
    String response = ipController.updateIpManually();

    verify(ipUpdaterMock, times(1)).updateIpAddress();
    assertEquals("IP updated!", response);
  }
}