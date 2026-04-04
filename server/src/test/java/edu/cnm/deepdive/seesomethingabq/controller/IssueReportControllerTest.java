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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import edu.cnm.deepdive.seesomethingabq.TestStorageConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
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
@ContextConfiguration(classes = {IssueReportControllerTest.TestConfig.class, TestStorageConfig.class})
class IssueReportControllerTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Autowired
  private IssueReportService issueReportService;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  @WithMockUser(roles = "USER")
  void postBindsFlattenedLocationFieldsAndDelegates() throws Exception {
    IssueReport created = new IssueReport();
    UUID externalId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    ReflectionTestUtils.setField(created, "externalId", externalId);
    when(issueReportService.createReport(org.mockito.ArgumentMatchers.any(IssueReportRequest.class)))
        .thenReturn(created);

    mockMvc.perform(
            post("/issue-reports")
                .with(csrf())
                .contentType("application/json")
                .content("""
                    {
                      "textDescription": "Graffiti on wall",
                      "latitude": 35.0847,
                      "longitude": -106.651,
                      "streetCoordinate": "Central Ave & 4th St",
                      "locationDescription": "Behind the storefront",
                      "issueTypes": ["Graffiti", "Trash"]
                    }
                    """)
        )
        .andExpect(status().isCreated());

    ArgumentCaptor<IssueReportRequest> captor = ArgumentCaptor.forClass(IssueReportRequest.class);
    verify(issueReportService).createReport(captor.capture());
    IssueReportRequest bound = captor.getValue();
    assertEquals("Graffiti on wall", bound.getTextDescription());
    assertEquals(35.0847, bound.getLatitude());
    assertEquals(-106.651, bound.getLongitude());
    assertEquals("Central Ave & 4th St", bound.getStreetCoordinate());
    assertEquals("Behind the storefront", bound.getLocationDescription());
    assertEquals(List.of("Graffiti", "Trash"), bound.getIssueTypes());
  }

  @Test
  @WithMockUser(roles = "USER")
  void putBindsIssueTypesArrayAndDelegates() throws Exception {
    UUID externalKey = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(issueReportService.updateReport(
        org.mockito.ArgumentMatchers.eq(externalKey),
        org.mockito.ArgumentMatchers.any(IssueReportRequest.class)
    )).thenReturn(new IssueReport());

    mockMvc.perform(
            put("/issue-reports/{externalKey}", externalKey)
                .with(csrf())
                .contentType("application/json")
                .content("""
                    {
                      "textDescription": "Updated",
                      "issueTypes": ["Trash"]
                    }
                    """)
        )
        .andExpect(status().isOk());

    ArgumentCaptor<IssueReportRequest> captor = ArgumentCaptor.forClass(IssueReportRequest.class);
    verify(issueReportService).updateReport(org.mockito.ArgumentMatchers.eq(externalKey), captor.capture());
    assertEquals("Updated", captor.getValue().getTextDescription());
    assertEquals(List.of("Trash"), captor.getValue().getIssueTypes());
  }

  @Test
  @WithMockUser(roles = "USER")
  void putWithEmptyIssueTypesArrayBindsAndDelegates() throws Exception {
    UUID externalKey = UUID.fromString("33333333-3333-3333-3333-333333333333");
    when(issueReportService.updateReport(
        org.mockito.ArgumentMatchers.eq(externalKey),
        org.mockito.ArgumentMatchers.any(IssueReportRequest.class)
    )).thenReturn(new IssueReport());

    mockMvc.perform(
            put("/issue-reports/{externalKey}", externalKey)
                .with(csrf())
                .contentType("application/json")
                .content("""
                    {
                      "textDescription": "Updated",
                      "issueTypes": []
                    }
                    """)
        )
        .andExpect(status().isOk());

    ArgumentCaptor<IssueReportRequest> captor = ArgumentCaptor.forClass(IssueReportRequest.class);
    verify(issueReportService).updateReport(org.mockito.ArgumentMatchers.eq(externalKey), captor.capture());
    assertEquals(List.of(), captor.getValue().getIssueTypes());
  }

  @Test
  @WithMockUser(roles = "USER")
  void putWithoutLocationFieldsIsAcceptedAndDelegates() throws Exception {
    UUID externalKey = UUID.fromString("44444444-4444-4444-4444-444444444444");
    when(issueReportService.updateReport(
        org.mockito.ArgumentMatchers.eq(externalKey),
        org.mockito.ArgumentMatchers.any(IssueReportRequest.class)
    )).thenReturn(new IssueReport());

    mockMvc.perform(
            put("/issue-reports/{externalKey}", externalKey)
                .with(csrf())
                .contentType("application/json")
                .content("""
                    {
                      "textDescription": "Only updating description",
                      "issueTypes": null
                    }
                    """)
        )
        .andExpect(status().isOk());

    ArgumentCaptor<IssueReportRequest> captor = ArgumentCaptor.forClass(IssueReportRequest.class);
    verify(issueReportService).updateReport(org.mockito.ArgumentMatchers.eq(externalKey), captor.capture());
    IssueReportRequest bound = captor.getValue();
    assertEquals("Only updating description", bound.getTextDescription());
    assertEquals(null, bound.getLatitude());
    assertEquals(null, bound.getLongitude());
    assertEquals(null, bound.getStreetCoordinate());
    assertEquals(null, bound.getLocationDescription());
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

