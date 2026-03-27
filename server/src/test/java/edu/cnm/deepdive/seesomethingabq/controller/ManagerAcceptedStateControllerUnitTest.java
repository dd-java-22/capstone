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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.seesomethingabq.model.dto.AcceptedStateDescriptionUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.service.AcceptedStateService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ManagerAcceptedStateControllerUnitTest {

  @Mock
  private AcceptedStateService acceptedStateService;

  private ManagerAcceptedStateController controller;

  @BeforeEach
  void setUp() {
    controller = new ManagerAcceptedStateController(acceptedStateService);
  }

  @Test
  void getAllReturnsFullListFromService() {
    List<AcceptedState> states = List.of(new AcceptedState(), new AcceptedState());
    when(acceptedStateService.getAll()).thenReturn(states);

    List<AcceptedState> result = controller.getAll();

    assertSame(states, result);
    verify(acceptedStateService).getAll();
  }

  @Test
  void createAcceptedStateReturnsCreatedEntity() {
    AcceptedState input = new AcceptedState();
    AcceptedState created = new AcceptedState();
    when(acceptedStateService.createNewAcceptedState(input)).thenReturn(created);

    AcceptedState result = controller.createAcceptedState(input);

    assertSame(created, result);
    verify(acceptedStateService).createNewAcceptedState(input);
  }

  @Test
  void createAcceptedStateDuplicateMapsTo409() {
    AcceptedState input = new AcceptedState();
    when(acceptedStateService.createNewAcceptedState(input)).thenThrow(new IllegalArgumentException());

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> controller.createAcceptedState(input));
    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    verify(acceptedStateService).createNewAcceptedState(input);
  }

  @Test
  void patchAcceptedStateDescriptionReturnsUpdatedEntity() {
    String statusTag = "OPEN";
    String newDescription = "New description";
    AcceptedStateDescriptionUpdateRequest request = new AcceptedStateDescriptionUpdateRequest();
    request.setStatusTagDescription(newDescription);
    AcceptedState updated = new AcceptedState();
    when(acceptedStateService.updateAcceptedStateDescription(statusTag, newDescription)).thenReturn(updated);

    AcceptedState result = controller.updateAcceptedStateDescription(statusTag, request);

    assertSame(updated, result);
    verify(acceptedStateService).updateAcceptedStateDescription(statusTag, newDescription);
  }

  @Test
  void patchAcceptedStateDescriptionMissingTagMapsTo404() {
    String statusTag = "MISSING";
    String newDescription = "New description";
    AcceptedStateDescriptionUpdateRequest request = new AcceptedStateDescriptionUpdateRequest();
    request.setStatusTagDescription(newDescription);
    when(acceptedStateService.updateAcceptedStateDescription(statusTag, newDescription))
        .thenThrow(new IllegalArgumentException());

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> controller.updateAcceptedStateDescription(statusTag, request));
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    verify(acceptedStateService).updateAcceptedStateDescription(statusTag, newDescription);
  }

  @Test
  void deleteAcceptedStateSuccessReturnsNormally() {
    String statusTag = "CLOSED";

    controller.deleteUnusedAcceptedState(statusTag);

    verify(acceptedStateService).deleteUnusedAcceptedState(statusTag);
  }

  @Test
  void deleteAcceptedStateMissingTagMapsTo404() {
    String statusTag = "MISSING";
    doThrow(new IllegalArgumentException())
        .when(acceptedStateService)
        .deleteUnusedAcceptedState(statusTag);

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> controller.deleteUnusedAcceptedState(statusTag));
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    verify(acceptedStateService).deleteUnusedAcceptedState(statusTag);
  }

  @Test
  void deleteAcceptedStateInUseMapsTo409() {
    String statusTag = "IN_USE";
    doThrow(new IllegalStateException())
        .when(acceptedStateService)
        .deleteUnusedAcceptedState(statusTag);

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> controller.deleteUnusedAcceptedState(statusTag));
    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    verify(acceptedStateService).deleteUnusedAcceptedState(statusTag);
  }

}
