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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary;
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportLocation;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.AcceptedStateRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueTypeRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

  private UserProfile currentUser;
  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    service = new IssueReportServiceImpl(
      issueReportRepository,
      userService,
      acceptedStateRepository,
      issueTypeRepository,
      validator
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

  @Test
  void getReportsForCurrentUser_lastModifiedAsc_ordersOldThenNew() {
    IssueReport older = new IssueReport();
    older.setTextDescription("OLDER");
    AcceptedState olderState = new AcceptedState();
    olderState.setStatusTag("OLDER_STATE");
    older.setAcceptedState(olderState);

    IssueReport newer = new IssueReport();
    newer.setTextDescription("NEWER");
    AcceptedState newerState = new AcceptedState();
    newerState.setStatusTag("NEWER_STATE");
    newer.setAcceptedState(newerState);

    UserProfile user = new UserProfile();
    when(userService.getCurrentUser()).thenReturn(user);

    when(issueReportRepository.findByUserProfile(any(UserProfile.class), any(Sort.class)))
        .thenAnswer(invocation -> {
          Sort sort = invocation.getArgument(1, Sort.class);
          Sort.Order order = sort.getOrderFor("timeLastModified");
          if (order != null && order.isAscending()) {
            return List.of(older, newer);
          } else {
            return List.of(newer, older);
          }
        });

    List<IssueReportSummary> results =
        service.getReportsForCurrentUser("last_modified,asc");

    assertThat(results)
        .extracting(IssueReportSummary::getDescription)
        .containsExactly("OLDER", "NEWER");
  }

  @Test
  void getReportsForCurrentUser_lastModifiedDesc_ordersNewThenOld() {
    IssueReport older = new IssueReport();
    older.setTextDescription("OLDER");
    AcceptedState olderState = new AcceptedState();
    olderState.setStatusTag("OLDER_STATE");
    older.setAcceptedState(olderState);

    IssueReport newer = new IssueReport();
    newer.setTextDescription("NEWER");
    AcceptedState newerState = new AcceptedState();
    newerState.setStatusTag("NEWER_STATE");
    newer.setAcceptedState(newerState);

    UserProfile user = new UserProfile();
    when(userService.getCurrentUser()).thenReturn(user);

    when(issueReportRepository.findByUserProfile(any(UserProfile.class), any(Sort.class)))
        .thenAnswer(invocation -> {
          Sort sort = invocation.getArgument(1, Sort.class);
          Sort.Order order = sort.getOrderFor("timeLastModified");
          if (order != null && order.isAscending()) {
            return List.of(older, newer);
          } else {
            return List.of(newer, older);
          }
        });

    List<IssueReportSummary> results =
        service.getReportsForCurrentUser("last_modified,desc");

    assertThat(results)
        .extracting(IssueReportSummary::getDescription)
        .containsExactly("NEWER", "OLDER");
  }

  @Test
  void getReportsForCurrentUser_parsesFirstReportedAscIntoSort() {
    UserProfile user = new UserProfile();
    when(userService.getCurrentUser()).thenReturn(user);
    when(issueReportRepository.findByUserProfile(any(UserProfile.class), any(Sort.class)))
        .thenReturn(List.of());

    ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);

    service.getReportsForCurrentUser("first_reported,asc");

    verify(issueReportRepository)
        .findByUserProfile(any(UserProfile.class), sortCaptor.capture());

    Sort sort = sortCaptor.getValue();
    Sort.Order order = sort.getOrderFor("timeFirstReported");
    assertThat(order).isNotNull();
    assertThat(order.isAscending()).isTrue();
  }

  @Test
  void createReport_validWithLatitudeAndLongitudeOnly_saves() {
    IssueReportRequest request = new IssueReportRequest();
    request.setTextDescription("TEST");
    request.setLatitude(35.1);
    request.setLongitude(-106.6);
    request.setIssueTypes(List.of());

    UserProfile user = new UserProfile();
    when(userService.getCurrentUser()).thenReturn(user);
    AcceptedState state = new AcceptedState();
    state.setStatusTag("New");
    when(acceptedStateRepository.findByStatusTag("New")).thenReturn(state);
    when(issueReportRepository.save(org.mockito.ArgumentMatchers.any(IssueReport.class)))
        .thenAnswer(invocation -> invocation.getArgument(0, IssueReport.class));

    IssueReport created = service.createReport(request);

    assertSame(user, created.getUserProfile());
    assertEquals("TEST", created.getTextDescription());
    assertThat(created.getReportLocation().getLatitude()).isEqualTo(35.1);
    assertThat(created.getReportLocation().getLongitude()).isEqualTo(-106.6);
    assertSame(created, created.getReportLocation().getIssueReport());
  }

  @Test
  void createReport_validWithStreetCoordinateOnly_saves() {
    IssueReportRequest request = new IssueReportRequest();
    request.setTextDescription("TEST");
    request.setStreetCoordinate(" Central & 4th ");
    request.setIssueTypes(List.of());

    when(userService.getCurrentUser()).thenReturn(new UserProfile());
    AcceptedState state = new AcceptedState();
    state.setStatusTag("New");
    when(acceptedStateRepository.findByStatusTag("New")).thenReturn(state);
    when(issueReportRepository.save(org.mockito.ArgumentMatchers.any(IssueReport.class)))
        .thenAnswer(invocation -> invocation.getArgument(0, IssueReport.class));

    IssueReport created = service.createReport(request);

    assertThat(created.getReportLocation().getStreetCoordinate()).isEqualTo(" Central & 4th ");
  }

  @Test
  void createReport_validWithLocationDescriptionOnly_saves() {
    IssueReportRequest request = new IssueReportRequest();
    request.setTextDescription("TEST");
    request.setLocationDescription(" Behind the store ");
    request.setIssueTypes(List.of());

    when(userService.getCurrentUser()).thenReturn(new UserProfile());
    AcceptedState state = new AcceptedState();
    state.setStatusTag("New");
    when(acceptedStateRepository.findByStatusTag("New")).thenReturn(state);
    when(issueReportRepository.save(org.mockito.ArgumentMatchers.any(IssueReport.class)))
        .thenAnswer(invocation -> invocation.getArgument(0, IssueReport.class));

    IssueReport created = service.createReport(request);

    assertThat(created.getReportLocation().getLocationDescription()).isEqualTo(" Behind the store ");
  }

  @Test
  void createReport_validWithStreetCoordinateAndOnlyOneCoordinate_saves() {
    IssueReportRequest request = new IssueReportRequest();
    request.setTextDescription("TEST");
    request.setLatitude(35.1);
    request.setStreetCoordinate("Central & 4th");
    request.setIssueTypes(List.of());

    when(userService.getCurrentUser()).thenReturn(new UserProfile());
    AcceptedState state = new AcceptedState();
    state.setStatusTag("New");
    when(acceptedStateRepository.findByStatusTag("New")).thenReturn(state);
    when(issueReportRepository.save(org.mockito.ArgumentMatchers.any(IssueReport.class)))
        .thenAnswer(invocation -> invocation.getArgument(0, IssueReport.class));

    IssueReport created = service.createReport(request);

    assertThat(created.getReportLocation().getLatitude()).isEqualTo(35.1);
    assertThat(created.getReportLocation().getLongitude()).isNull();
    assertThat(created.getReportLocation().getStreetCoordinate()).isEqualTo("Central & 4th");
  }

  @Test
  void createReport_validWithLocationDescriptionAndOnlyOneCoordinate_saves() {
    IssueReportRequest request = new IssueReportRequest();
    request.setTextDescription("TEST");
    request.setLongitude(-106.6);
    request.setLocationDescription("Behind store");
    request.setIssueTypes(List.of());

    when(userService.getCurrentUser()).thenReturn(new UserProfile());
    AcceptedState state = new AcceptedState();
    state.setStatusTag("New");
    when(acceptedStateRepository.findByStatusTag("New")).thenReturn(state);
    when(issueReportRepository.save(org.mockito.ArgumentMatchers.any(IssueReport.class)))
        .thenAnswer(invocation -> invocation.getArgument(0, IssueReport.class));

    IssueReport created = service.createReport(request);

    assertThat(created.getReportLocation().getLatitude()).isNull();
    assertThat(created.getReportLocation().getLongitude()).isEqualTo(-106.6);
    assertThat(created.getReportLocation().getLocationDescription()).isEqualTo("Behind store");
  }

  @Test
  void createReport_invalidWhenAllLocationOptionsAbsent_throws() {
    IssueReportRequest request = new IssueReportRequest();
    request.setTextDescription("TEST");
    request.setStreetCoordinate("   ");
    request.setLocationDescription(null);
    request.setLatitude(35.1);
    request.setLongitude(null);
    request.setIssueTypes(List.of());

    when(userService.getCurrentUser()).thenReturn(new UserProfile());
    AcceptedState state = new AcceptedState();
    state.setStatusTag("New");
    when(acceptedStateRepository.findByStatusTag("New")).thenReturn(state);

    assertThrows(ConstraintViolationException.class, () -> service.createReport(request));
  }

  @Test
  void updateReport_updatesReportLocationFieldsInPlace() {
    UUID externalId = UUID.fromString("99999999-9999-9999-9999-999999999999");
    IssueReport existing = new IssueReport();
    ReportLocation location = new ReportLocation();
    location.setIssueReport(existing);
    location.setLatitude(1.0);
    location.setLongitude(2.0);
    location.setStreetCoordinate("OLD");
    location.setLocationDescription("OLD DESC");
    existing.setReportLocation(location);

    when(issueReportRepository.findByExternalId(externalId)).thenReturn(Optional.of(existing));
    when(issueReportRepository.save(existing)).thenReturn(existing);

    IssueReportRequest request = new IssueReportRequest();
    request.setTextDescription("UPDATED");
    request.setLatitude(35.0844);
    request.setLongitude(-106.6504);
    request.setStreetCoordinate("Central Ave & 4th St");
    request.setLocationDescription("Same corner");
    request.setIssueTypes(null); // ensure issueTypes behavior remains unchanged for this test

    IssueReport updated = service.updateReport(externalId, request);

    assertSame(existing, updated);
    assertEquals("UPDATED", updated.getTextDescription());
    assertSame(location, updated.getReportLocation());
    assertThat(updated.getReportLocation().getLatitude()).isEqualTo(35.0844);
    assertThat(updated.getReportLocation().getLongitude()).isEqualTo(-106.6504);
    assertThat(updated.getReportLocation().getStreetCoordinate()).isEqualTo("Central Ave & 4th St");
    assertThat(updated.getReportLocation().getLocationDescription()).isEqualTo("Same corner");
    assertSame(updated, updated.getReportLocation().getIssueReport());
  }
}
