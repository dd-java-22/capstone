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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.UserService;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("service")
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://example.com/issuer",
    "spring.security.oauth2.resourceserver.jwt.audiences=test-client-id"
})
@ContextConfiguration(classes = {ManagerUserControllerTest.TestConfig.class})
class ManagerUserControllerTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Autowired
  private JwtDecoder jwtDecoder;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  void getAllForbiddenForNonManager() throws Exception {
    mockMvc.perform(get("/manager/users"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "USER")
  void getAllForbiddenForUserRole() throws Exception {
    mockMvc.perform(get("/manager/users"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "MANAGER")
  void getAllAllowedForManagerRole() throws Exception {
    UUID externalId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    UserProfile user = buildUser(
        externalId,
        "Manager User",
        "manager@example.com",
        new URL("https://example.com/avatars/manager.png"),
        true,
        Instant.parse("2026-03-06T10:15:30Z"),
        true
    );
    when(userService.getAll()).thenReturn(List.of(user));
    mockMvc.perform(get("/manager/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", Matchers.hasSize(1)))
        .andExpect(jsonPath("$[0].externalId").value(externalId.toString()))
        .andExpect(jsonPath("$[0].displayName").value("Manager User"))
        .andExpect(jsonPath("$[0].email").value("manager@example.com"))
        .andExpect(jsonPath("$[0].manager").value(true))
        .andExpect(jsonPath("$[0].userEnabled").value(true))
        .andExpect(jsonPath("$[0].id").doesNotExist())
        .andExpect(jsonPath("$[0].oauthKey").doesNotExist())
        .andExpect(jsonPath("$[0].issueReports").doesNotExist());
  }

  @Test
  @WithMockUser(roles = "MANAGER")
  void getByExternalIdReturnsExpectedShape() throws Exception {
    UUID externalId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    UserProfile user = buildUser(
        externalId,
        "Riley Reporter",
        "user@example.com",
        new URL("https://example.com/avatars/user.png"),
        false,
        Instant.parse("2026-03-07T10:15:30Z"),
        false
    );
    when(userService.getByExternalId(externalId)).thenReturn(Optional.of(user));
    mockMvc.perform(get("/manager/users/{externalId}", externalId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.externalId").value(externalId.toString()))
        .andExpect(jsonPath("$.displayName").value("Riley Reporter"))
        .andExpect(jsonPath("$.email").value("user@example.com"))
        .andExpect(jsonPath("$.manager").value(false))
        .andExpect(jsonPath("$.userEnabled").value(false))
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.oauthKey").doesNotExist())
        .andExpect(jsonPath("$.issueReports").doesNotExist());
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    @Primary
    public UserService userService() {
      return org.mockito.Mockito.mock(UserService.class);
    }

    @Bean(name = "provideDecoder")
    public JwtDecoder provideDecoder() {
      return org.mockito.Mockito.mock(JwtDecoder.class);
    }

  }

  private static UserProfile buildUser(UUID externalId, String displayName, String email, URL avatar,
      boolean isManager, Instant timeCreated, boolean userEnabled) {
    UserProfile userProfile = new UserProfile();
    userProfile.setOauthKey("oauth-key");
    userProfile.setDisplayName(displayName);
    userProfile.setEmail(email);
    userProfile.setAvatar(avatar);
    userProfile.setIsManager(isManager);
    userProfile.setUserEnabled(userEnabled);
    ReflectionTestUtils.setField(userProfile, "externalId", externalId);
    ReflectionTestUtils.setField(userProfile, "timeCreated", timeCreated);
    return userProfile;
  }

}
