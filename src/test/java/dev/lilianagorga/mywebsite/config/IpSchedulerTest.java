package dev.lilianagorga.mywebsite.config;

import dev.lilianagorga.mywebsite.AbstractTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
class IpSchedulerTest extends AbstractTestConfig {

  private IpScheduler ipScheduler;
  private MongoDBIpUpdater ipUpdaterMock;

  @BeforeEach
  void setUp() {
    ipUpdaterMock = Mockito.mock(MongoDBIpUpdater.class);
    ipScheduler = new IpScheduler(ipUpdaterMock);
  }

  @Test
  void updateIp_shouldInvokeIpUpdater() {
    ipScheduler.updateIp();
    verify(ipUpdaterMock, times(1)).updateIpAddress();
  }

  @Test
  void updateIp_shouldHandleExceptionGracefully() {
    doThrow(new RuntimeException("Test exception")).when(ipUpdaterMock).updateIpAddress();

    ipScheduler.updateIp();

    verify(ipUpdaterMock, times(1)).updateIpAddress();
  }
}