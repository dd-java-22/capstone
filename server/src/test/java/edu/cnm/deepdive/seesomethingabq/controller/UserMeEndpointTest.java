/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.seesomethingabq.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.seesomethingabq.TestStorageConfig;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.UserProfileRepository;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("service")
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://example.com/issuer"
})
@ContextConfiguration(classes = {UserMeEndpointTest.TestConfig.class, TestStorageConfig.class})
class UserMeEndpointTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private UserProfileRepository userProfileRepository;

  @Autowired
  private JwtDecoder jwtDecoder;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
    userProfileRepository.deleteAll();
  }

  @Test
  void getMeCreatesEnabledUserWhenMissing() throws Exception {
    String subject = "sub-new-user";
    URL avatar = new URL("https://example.com/avatar.png");
    Jwt jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .subject(subject)
        .claim("name", "New User")
        .claim("email", "new.user@example.com")
        .claim("picture", avatar)
        .build();
    when(jwtDecoder.decode("token")).thenReturn(jwt);
    mockMvc
        .perform(get("/users/me")
            .header("Authorization", "Bearer token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userEnabled").value(true));

    UserProfile persisted = userProfileRepository
        .findByOauthKey(subject)
        .orElseThrow();
    assertTrue(persisted.getUserEnabled());
  }

  @TestConfiguration
  static class TestConfig {

    /**
     * The filter chain requires a {@link JwtDecoder} bean to exist, even when tests supply JWTs via
     * request post-processors.
     */
    @Bean(name = "provideDecoder")
    @Primary
    public JwtDecoder provideDecoder() {
      return org.mockito.Mockito.mock(JwtDecoder.class);
    }

  }

}
