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
package edu.cnm.deepdive.seesomethingabq.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.AcceptedStateRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueTypeRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class IssueReportServiceImplTest {

  @Mock
  private IssueReportRepository issueReportRepository;

  @Mock
  private AcceptedStateRepository acceptedStateRepository;

  @Mock
  private IssueTypeRepository issueTypeRepository;

  @Mock
  private UserService userService;

  private IssueReportService service;

  @BeforeEach
  void setUp() {
    service = new IssueReportServiceImpl(
      issueReportRepository,
      userService,
      acceptedStateRepository,
      issueTypeRepository
    );
  }

  @Test
  void getAllReturnsPageFromRepository() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<IssueReport> page = new PageImpl<>(List.of(new IssueReport(), new IssueReport()));
    when(issueReportRepository.findAll(pageable)).thenReturn(page);

    Page<IssueReport> result = service.getAll(pageable);

    assertSame(page, result);
    verify(issueReportRepository).findAll(pageable);
  }

  @Test
  void getByExternalIdFoundReturnsOptionalWithValue() {
    UUID externalId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    IssueReport report = new IssueReport();
    when(issueReportRepository.findByExternalId(externalId)).thenReturn(Optional.of(report));

    Optional<IssueReport> result = service.getByExternalId(externalId);

    assertSame(report, result.orElseThrow());
    verify(issueReportRepository).findByExternalId(externalId);
  }

  @Test
  void getByExternalIdNotFoundReturnsEmptyOptional() {
    UUID externalId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(issueReportRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

    Optional<IssueReport> result = service.getByExternalId(externalId);

    assertEquals(Optional.empty(), result);
    verify(issueReportRepository).findByExternalId(externalId);
  }

  @Test
  void setAcceptedStateWhenBothExistSavesAndReturnsUpdatedReport() {
    UUID externalId = UUID.fromString("33333333-3333-3333-3333-333333333333");
    IssueReport report = new IssueReport();
    AcceptedState state = new AcceptedState();
    state.setStatusTag("ACCEPTED");
    when(issueReportRepository.findByExternalId(externalId)).thenReturn(Optional.of(report));
    when(acceptedStateRepository.findByStatusTag("ACCEPTED")).thenReturn(state);
    when(issueReportRepository.save(report)).thenReturn(report);

    IssueReport result = service.setAcceptedState(externalId, "ACCEPTED");

    assertSame(report, result);
    assertSame(state, report.getAcceptedState());
    verify(issueReportRepository).findByExternalId(externalId);
    verify(acceptedStateRepository).findByStatusTag("ACCEPTED");
    verify(issueReportRepository).save(report);
  }

  @Test
  void setAcceptedStateMissingReportThrows() {
    UUID externalId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    when(issueReportRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> service.setAcceptedState(externalId, "ACCEPTED"));
    verify(issueReportRepository).findByExternalId(externalId);
  }

  @Test
  void setAcceptedStateMissingAcceptedStateThrows() {
    UUID externalId = UUID.fromString("55555555-5555-5555-5555-555555555555");
    IssueReport report = new IssueReport();
    when(issueReportRepository.findByExternalId(externalId)).thenReturn(Optional.of(report));
    when(acceptedStateRepository.findByStatusTag("MISSING")).thenReturn(null);

    assertThrows(NoSuchElementException.class, () -> service.setAcceptedState(externalId, "MISSING"));
    verify(issueReportRepository).findByExternalId(externalId);
    verify(acceptedStateRepository).findByStatusTag("MISSING");
  }

  @Test
  void replaceIssueTypesWhenAllTagsExistReplacesAndSaves() {
    UUID externalId = UUID.fromString("66666666-6666-6666-6666-666666666666");
    IssueReport report = new IssueReport();
    IssueType existing = new IssueType();
    existing.setIssueTypeTag("OLD");
    report.getIssueTypes().add(existing);

    IssueType pothole = new IssueType();
    pothole.setIssueTypeTag("POTHOLE");
    IssueType graffiti = new IssueType();
    graffiti.setIssueTypeTag("GRAFFITI");

    when(issueReportRepository.findByExternalId(externalId)).thenReturn(Optional.of(report));
    when(issueTypeRepository.findAllByIssueTypeTagIn(org.mockito.ArgumentMatchers.anyCollection()))
        .thenReturn(List.of(pothole, graffiti));
    when(issueReportRepository.save(report)).thenReturn(report);

    IssueReport result = service.replaceIssueTypes(externalId, List.of("POTHOLE", "GRAFFITI"));

    assertSame(report, result);
    assertEquals(2, report.getIssueTypes().size());
    assertSame(pothole, report.getIssueTypes().get(0));
    assertSame(graffiti, report.getIssueTypes().get(1));
    verify(issueReportRepository).findByExternalId(externalId);
    verify(issueTypeRepository).findAllByIssueTypeTagIn(
        org.mockito.ArgumentMatchers.argThat(tags ->
            tags != null && tags.size() == 2 && tags.containsAll(List.of("POTHOLE", "GRAFFITI")))
    );
    verify(issueReportRepository).save(report);
  }

  @Test
  void replaceIssueTypesMissingReportThrows() {
    UUID externalId = UUID.fromString("77777777-7777-7777-7777-777777777777");
    when(issueReportRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class,
        () -> service.replaceIssueTypes(externalId, List.of("POTHOLE")));
    verify(issueReportRepository).findByExternalId(externalId);
  }

  @Test
  void replaceIssueTypesInvalidTagSetThrows() {
    UUID externalId = UUID.fromString("88888888-8888-8888-8888-888888888888");
    IssueReport report = new IssueReport();
    when(issueReportRepository.findByExternalId(externalId)).thenReturn(Optional.of(report));
    when(issueTypeRepository.findAllByIssueTypeTagIn(org.mockito.ArgumentMatchers.anyCollection()))
        .thenReturn(List.of(new IssueType()));

    assertThrows(IllegalArgumentException.class,
        () -> service.replaceIssueTypes(externalId, List.of("POTHOLE", "INVALID")));
    verify(issueReportRepository).findByExternalId(externalId);
    verify(issueTypeRepository).findAllByIssueTypeTagIn(
        org.mockito.ArgumentMatchers.argThat(tags ->
            tags != null && tags.size() == 2 && tags.containsAll(List.of("POTHOLE", "INVALID")))
    );
  }

}
