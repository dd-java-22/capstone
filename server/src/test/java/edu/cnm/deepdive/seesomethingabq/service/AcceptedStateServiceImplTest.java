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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

Kimport edu.cnm.deepdive.seesomethingabq.exception.AcceptedStateNotFoundException;
import edu.cnm.deepdive.seesomethingabq.exception.ConflictException;
import edu.cnm.deepdive.seesomethingabq.exception.DuplicateAcceptedStateException;
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.service.repository.AcceptedStateRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link AcceptedStateServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AcceptedStateServiceImplTest {

  @Mock
  private AcceptedStateRepository repository;

  @InjectMocks
  private AcceptedStateServiceImpl service;

  @Test
  void testGetAllReturnsSortedRepositoryResults() {
    List<AcceptedState> states = List.of(new AcceptedState(), new AcceptedState());
    when(repository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(states);

    List<AcceptedState> result = service.getAll();

    assertSame(states, result);
    verify(repository, times(1)).findAll(any(org.springframework.data.domain.Sort.class));
  }

  @Test
  void testCreateNewAcceptedStateSuccess() {
    AcceptedState input = new AcceptedState();
    input.setStatusTag("OPEN");
    when(repository.existsByStatusTag("OPEN")).thenReturn(false);
    when(repository.save(input)).thenReturn(input);

    AcceptedState result = service.createNewAcceptedState(input);

    assertSame(input, result);
    verify(repository, times(1)).save(input);
  }

  @Test
  void testCreateNewAcceptedStateDuplicateThrows() {
    AcceptedState input = new AcceptedState();
    input.setStatusTag("DUP");
    when(repository.existsByStatusTag("DUP")).thenReturn(true);

    assertThrows(DuplicateAcceptedStateException.class, () -> service.createNewAcceptedState(input));

    verify(repository, never()).save(any(AcceptedState.class));
  }

  @Test
  void testUpdateAcceptedStateDescriptionSuccess() {
    String statusTag = "OPEN";
    AcceptedState existing = new AcceptedState();
    existing.setStatusTag(statusTag);
    existing.setStatusTagDescription("Old");
    when(repository.findByStatusTag(statusTag)).thenReturn(existing);
    when(repository.save(any(AcceptedState.class))).thenAnswer(invocation -> invocation.getArgument(0));

    AcceptedState result = service.updateAcceptedStateDescription(statusTag, "New");

    assertSame(existing, result);
    assertEquals("New", existing.getStatusTagDescription());
    verify(repository, times(1)).save(existing);
  }

  @Test
  void testUpdateAcceptedStateDescriptionMissingTagThrows() {
    when(repository.findByStatusTag("MISSING")).thenReturn(null);

    assertThrows(AcceptedStateNotFoundException.class,
        () -> service.updateAcceptedStateDescription("MISSING", "New"));

    verify(repository, never()).save(any(AcceptedState.class));
  }

  @Test
  void testDeleteUnusedAcceptedStateSuccess() {
    AcceptedState doomed = new AcceptedState();
    doomed.setStatusTag("CLOSED");
    when(repository.findByStatusTag("CLOSED")).thenReturn(doomed);

    service.deleteUnusedAcceptedState("CLOSED");

    verify(repository, times(1)).delete(doomed);
  }

  @Test
  void testDeleteUnusedAcceptedStateMissingTagThrows() {
    when(repository.findByStatusTag("MISSING")).thenReturn(null);

    assertThrows(AcceptedStateNotFoundException.class, () -> service.deleteUnusedAcceptedState("MISSING"));

    verify(repository, never()).delete(any(AcceptedState.class));
  }

  @Test
  void testDeleteUnusedAcceptedStateInUseThrows() {
    AcceptedState doomed = new AcceptedState();
    doomed.setStatusTag("IN_USE");
    doomed.getIssueReports().add(new IssueReport());
    when(repository.findByStatusTag("IN_USE")).thenReturn(doomed);

    assertThrows(ConflictException.class, () -> service.deleteUnusedAcceptedState("IN_USE"));

    verify(repository, never()).delete(any(AcceptedState.class));
  }

}

