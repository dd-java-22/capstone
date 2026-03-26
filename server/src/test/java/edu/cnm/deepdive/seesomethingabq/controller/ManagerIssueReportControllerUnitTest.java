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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportStatusUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportTypesUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ManagerIssueReportControllerUnitTest {

  @Mock
  private IssueReportService issueReportService;

  private ManagerIssueReportController controller;

  @BeforeEach
  void setUp() {
    controller = new ManagerIssueReportController(issueReportService);
  }

  @Test
  void getAllDelegatesToServiceAndReturnsPage() {
    Page<IssueReport> page = new PageImpl<>(List.of(new IssueReport(), new IssueReport()));
    when(issueReportService.getAll(org.mockito.ArgumentMatchers.any(Pageable.class)))
        .thenReturn(page);

    Page<IssueReport> result = controller.getAll(10, 0);

    assertSame(page, result);
    verify(issueReportService).getAll(org.mockito.ArgumentMatchers.any(Pageable.class));
  }

  @Test
  void updateStatusReturnsUpdatedIssueReport() {
    UUID externalId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    IssueReportStatusUpdateRequest request = new IssueReportStatusUpdateRequest();
    request.setStatusTag("ACCEPTED");
    IssueReport updated = new IssueReport();
    when(issueReportService.setAcceptedState(externalId, "ACCEPTED")).thenReturn(updated);

    IssueReport result = controller.updateStatus(externalId, request);

    assertSame(updated, result);
    verify(issueReportService).setAcceptedState(externalId, "ACCEPTED");
  }

  @Test
  void updateStatusMissingReportOrStateMapsTo404() {
    UUID externalId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    IssueReportStatusUpdateRequest request = new IssueReportStatusUpdateRequest();
    request.setStatusTag("ACCEPTED");
    when(issueReportService.setAcceptedState(externalId, "ACCEPTED"))
        .thenThrow(new NoSuchElementException());

    ResponseStatusException ex = assertThrows(
        ResponseStatusException.class,
        () -> controller.updateStatus(externalId, request)
    );
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void updateStatusMissingAcceptedStateMapsTo404() {
    UUID externalId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    IssueReportStatusUpdateRequest request = new IssueReportStatusUpdateRequest();
    request.setStatusTag("MISSING");
    when(issueReportService.setAcceptedState(externalId, "MISSING"))
        .thenThrow(new NoSuchElementException());

    ResponseStatusException ex = assertThrows(
        ResponseStatusException.class,
        () -> controller.updateStatus(externalId, request)
    );
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void updateIssueTypesReturnsUpdatedIssueReport() {
    UUID externalId = UUID.fromString("33333333-3333-3333-3333-333333333333");
    IssueReportTypesUpdateRequest request = new IssueReportTypesUpdateRequest();
    request.setIssueTypeTags(List.of("POTHOLE", "GRAFFITI"));
    IssueReport updated = new IssueReport();
    when(issueReportService.replaceIssueTypes(externalId, List.of("POTHOLE", "GRAFFITI")))
        .thenReturn(updated);

    IssueReport result = controller.updateIssueTypes(externalId, request);

    assertSame(updated, result);
    verify(issueReportService).replaceIssueTypes(externalId, List.of("POTHOLE", "GRAFFITI"));
  }

  @Test
  void updateIssueTypesMissingReportMapsTo404() {
    UUID externalId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    IssueReportTypesUpdateRequest request = new IssueReportTypesUpdateRequest();
    request.setIssueTypeTags(List.of("POTHOLE"));
    when(issueReportService.replaceIssueTypes(externalId, List.of("POTHOLE")))
        .thenThrow(new NoSuchElementException());

    ResponseStatusException ex = assertThrows(
        ResponseStatusException.class,
        () -> controller.updateIssueTypes(externalId, request)
    );
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void updateIssueTypesInvalidTagsMapsTo400() {
    UUID externalId = UUID.fromString("55555555-5555-5555-5555-555555555555");
    IssueReportTypesUpdateRequest request = new IssueReportTypesUpdateRequest();
    request.setIssueTypeTags(List.of("INVALID"));
    when(issueReportService.replaceIssueTypes(externalId, List.of("INVALID")))
        .thenThrow(new IllegalArgumentException());

    ResponseStatusException ex = assertThrows(
        ResponseStatusException.class,
        () -> controller.updateIssueTypes(externalId, request)
    );
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
  }

}
