package edu.cnm.deepdive.seesomethingabq.configuration;

import edu.cnm.deepdive.seesomethingabq.SeeSomethingAbqApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationHomeConfiguration {

  @Bean
  public ApplicationHome provideApplicationHome() {
    return new ApplicationHome(SeeSomethingAbqApplication.class);
  }

}
