package edu.cnm.deepdive.seesomethingabq;

import edu.cnm.deepdive.seesomethingabq.service.storage.LocalFileSystemStorageService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestStorageConfig {

  @Bean(name = "localFileSystemStorageService")
  @Primary
  public LocalFileSystemStorageService localFileSystemStorageService() {
    return Mockito.mock(LocalFileSystemStorageService.class);
  }

}
