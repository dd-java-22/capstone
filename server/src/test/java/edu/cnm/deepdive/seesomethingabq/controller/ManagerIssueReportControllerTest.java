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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
@ContextConfiguration(classes = {ManagerIssueReportControllerTest.TestConfig.class})
class ManagerIssueReportControllerTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Autowired
  private IssueReportService issueReportService;

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
    mockMvc.perform(get("/manager/issue-reports"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "USER")
  void getAllForbiddenForUserRole() throws Exception {
    mockMvc.perform(get("/manager/issue-reports"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "MANAGER")
  void getAllAllowedForManagerRole() throws Exception {
    Page<IssueReport> page = new PageImpl<>(java.util.List.of());
    when(issueReportService.getAll(org.mockito.ArgumentMatchers.any())).thenReturn(page);
    mockMvc.perform(get("/manager/issue-reports"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "MANAGER")
  void getAllWithInvalidPageNumberReturns400() throws Exception {
    mockMvc.perform(get("/manager/issue-reports").param("pageNumber", "not-an-int"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "MANAGER")
  void putStatusWithInvalidExternalIdReturns400() throws Exception {
    mockMvc.perform(
            put("/manager/issue-reports/{externalId}/status", "not-a-uuid")
                .with(csrf())
                .contentType("application/json")
                .content("{\"statusTag\":\"ACCEPTED\"}")
        )
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "MANAGER")
  void putStatusAllowedForManagerRole() throws Exception {
    UUID externalId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    when(issueReportService.setAcceptedState(externalId, "ACCEPTED")).thenReturn(new IssueReport());
    mockMvc.perform(
            put("/manager/issue-reports/{externalId}/status", externalId)
                .with(csrf())
                .contentType("application/json")
                .content("{\"statusTag\":\"ACCEPTED\"}")
        )
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "MANAGER")
  void putIssueTypesAllowedForManagerRole() throws Exception {
    UUID externalId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(issueReportService.replaceIssueTypes(
        org.mockito.ArgumentMatchers.eq(externalId),
        org.mockito.ArgumentMatchers.any()
    )).thenReturn(new IssueReport());
    mockMvc.perform(
            put("/manager/issue-reports/{externalId}/issue-types", externalId)
                .with(csrf())
                .contentType("application/json")
                .content("{\"issueTypeTags\":[\"POTHOLE\",\"GRAFFITI\"]}")
        )
        .andExpect(status().isOk());
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    @Primary
    public IssueReportService issueReportService() {
      return org.mockito.Mockito.mock(IssueReportService.class);
    }

    @Bean(name = "provideDecoder")
    public JwtDecoder provideDecoder() {
      return org.mockito.Mockito.mock(JwtDecoder.class);
    }

  }

}
