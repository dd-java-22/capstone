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

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.cnm.deepdive.seesomethingabq.TestStorageConfig;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.service.IssueTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
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
    "spring.security.oauth2.resourceserver.jwt.audiences=test-client-id"
})
@ContextConfiguration(classes = {ManagerIssueTypeControllerTest.TestConfig.class, TestStorageConfig.class})
class ManagerIssueTypeControllerTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Autowired
  private IssueTypeService issueTypeService;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  @WithMockUser(roles = "MANAGER")
  void createIssueTypeReturnsLocation() throws Exception {
    IssueType created = new IssueType();
    created.setIssueTypeTag("POTHOLE");
    created.setIssueTypeDescription("Pothole");
    when(issueTypeService.createNewIssueType(org.mockito.ArgumentMatchers.any(IssueType.class)))
        .thenReturn(created);

    mockMvc.perform(
            post("/manager/issue-types")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "issueTypeTag": "POTHOLE",
                      "issueTypeDescription": "Pothole"
                    }
                    """)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string("Location",
            endsWith("/manager/issue-types/POTHOLE")))
        .andExpect(jsonPath("$.issueTypeTag").value("POTHOLE"));
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    @Primary
    public IssueTypeService issueTypeService() {
      return org.mockito.Mockito.mock(IssueTypeService.class);
    }

    @Bean(name = "provideDecoder")
    public JwtDecoder provideDecoder() {
      return org.mockito.Mockito.mock(JwtDecoder.class);
    }
  }
}
