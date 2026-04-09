package edu.cnm.deepdive.seesomethingabq.configuration;

import edu.cnm.deepdive.seesomethingabq.SeeSomethingAbqApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration providing an {@link ApplicationHome} rooted at the server application
 * class location.
 */
@Configuration
public class ApplicationHomeConfiguration {

  /**
   * Provides an {@link ApplicationHome} instance for resolving paths relative to the running
   * application.
   *
   * @return application home wrapper.
   */
  @Bean
  public ApplicationHome provideApplicationHome() {
    return new ApplicationHome(SeeSomethingAbqApplication.class);
  }

}
