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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.seesomethingabq.TestStorageConfig;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import edu.cnm.deepdive.seesomethingabq.service.repository.UserProfileRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://example.com/issuer",
    "spring.security.oauth2.resourceserver.jwt.client-id=test-client-id"
})
@ContextConfiguration(classes = {UserEnabledAuthorizationTest.TestConfig.class, TestStorageConfig.class})
class UserEnabledAuthorizationTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private JwtDecoder jwtDecoder;

  @Autowired
  private UserProfileRepository userProfileRepository;

  @Autowired
  private IssueReportService issueReportService;

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
  void disabledUserCannotAccessOtherEndpoints() throws Exception {
    UserProfile user = new UserProfile();
    user.setOauthKey("sub-disabled");
    user.setDisplayName("Disabled User");
    user.setEmail("disabled@example.com");
    user.setUserEnabled(false);
    userProfileRepository.save(user);

    Jwt jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .subject("sub-disabled")
        .claim("name", "Disabled User")
        .claim("email", "disabled@example.com")
        .build();
    when(jwtDecoder.decode("token")).thenReturn(jwt);

    mockMvc.perform(
            put("/users/me/email")
                .with(csrf())
                .header("Authorization", "Bearer token")
                .contentType("application/json")
                .content("{\"email\":\"updated@example.com\"}")
        )
        .andExpect(status().isForbidden());
  }

  @Test
  void enabledUserCanAccessOtherEndpoints() throws Exception {
    UserProfile user = new UserProfile();
    user.setOauthKey("sub-enabled");
    user.setDisplayName("Enabled User");
    user.setEmail("enabled@example.com");
    user.setUserEnabled(true);
    userProfileRepository.save(user);

    Jwt jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .subject("sub-enabled")
        .claim("name", "Enabled User")
        .claim("email", "enabled@example.com")
        .build();
    when(jwtDecoder.decode("token")).thenReturn(jwt);

    mockMvc.perform(
            put("/users/me/email")
                .with(csrf())
                .header("Authorization", "Bearer token")
                .contentType("application/json")
                .content("{\"email\":\"updated@example.com\"}")
        )
        .andExpect(status().isOk());
  }

  @Test
  void disabledUserCanStillGetUsersMe() throws Exception {
    UserProfile user = new UserProfile();
    user.setOauthKey("sub-disabled-me");
    user.setDisplayName("Disabled User");
    user.setEmail("disabled@example.com");
    user.setUserEnabled(false);
    userProfileRepository.save(user);

    Jwt jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .subject("sub-disabled-me")
        .claim("name", "Disabled User")
        .claim("email", "disabled@example.com")
        .build();
    when(jwtDecoder.decode("token")).thenReturn(jwt);

    mockMvc.perform(get("/users/me").header("Authorization", "Bearer token"))
        .andExpect(status().isOk());
  }

  @Test
  void disabledManagerCannotAccessManagerEndpoints() throws Exception {
    UserProfile user = new UserProfile();
    user.setOauthKey("sub-disabled-manager");
    user.setDisplayName("Disabled Manager");
    user.setEmail("disabled.manager@example.com");
    user.setUserEnabled(false);
    user.setIsManager(true);
    userProfileRepository.save(user);

    Jwt jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .subject("sub-disabled-manager")
        .claim("name", "Disabled Manager")
        .claim("email", "disabled.manager@example.com")
        .build();
    when(jwtDecoder.decode("token")).thenReturn(jwt);

    Page<IssueReport> page = new PageImpl<>(List.of());
    when(issueReportService.getAll(org.mockito.ArgumentMatchers.any())).thenReturn(page);

    mockMvc.perform(get("/manager/issue-reports").header("Authorization", "Bearer token"))
        .andExpect(status().isForbidden());
  }

  @TestConfiguration
  static class TestConfig {

    @Bean(name = "provideDecoder")
    @Primary
    public JwtDecoder provideDecoder() {
      return org.mockito.Mockito.mock(JwtDecoder.class);
    }

    @Bean
    @Primary
    public IssueReportService issueReportService() {
      return org.mockito.Mockito.mock(IssueReportService.class);
    }

  }

}
