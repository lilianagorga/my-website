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
  void updateIp_shouldNotInvokeIpUpdaterWhenDisabled() {
    ipScheduler.updateIp();
    verify(ipUpdaterMock, never()).updateIpAddress();
  }

  @Test
  void onStartup_shouldNotInvokeIpUpdaterWhenDisabled() {
    ipScheduler.onStartup();
    verify(ipUpdaterMock, never()).updateIpAddress();
  }
}