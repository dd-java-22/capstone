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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.seesomethingabq.exception.UserNotFoundException;
import edu.cnm.deepdive.seesomethingabq.model.dto.ManagerStatusUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.UserEnabledUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.UserService;
import java.util.List;
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
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class ManagerUserControllerUnitTest {

  @Mock
  private UserService userService;

  private ManagerUserController controller;

  @BeforeEach
  void setUp() {
    controller = new ManagerUserController(userService);
  }

  @Test
  void getAllReturnsFullListFromService() {
    List<UserProfile> users = List.of(new UserProfile(), new UserProfile());
    Page<UserProfile> page = new PageImpl<>(users);
    when(userService.getAll(any(Pageable.class))).thenReturn(page);

    int pageSize = 20;
    int pageNumber = 0;
    Page<UserProfile> result = controller.getAll(pageSize, pageNumber);

    assertSame(page, result);
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(userService).getAll(pageableCaptor.capture());
    Pageable pageable = pageableCaptor.getValue();
    assertEquals(PageRequest.of(pageNumber, pageSize, org.springframework.data.domain.Sort.by(Direction.ASC, "displayName")),
        pageable);
  }

  @Test
  void getByExternalIdReturnsRequestedUser() {
    UUID externalId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    UserProfile user = new UserProfile();
    user.setOauthKey("oauth-key");
    user.setDisplayName("User");
    user.setEmail("user@example.com");
    when(userService.getByExternalId(externalId)).thenReturn(Optional.of(user));

    UserProfile result = controller.get(externalId);

    assertNotNull(result);
    assertSame(user, result);
    verify(userService).getByExternalId(externalId);
  }

  @Test
  void getByExternalIdNotFoundMapsTo404() {
    UUID externalId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(userService.getByExternalId(externalId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> controller.get(externalId));
    verify(userService).getByExternalId(externalId);
  }

  @Test
  void patchManagerStatusPassesBooleanThroughAndReturnsUpdatedUser() {
    UUID externalId = UUID.fromString("33333333-3333-3333-3333-333333333333");
    ManagerStatusUpdateRequest request = new ManagerStatusUpdateRequest();
    request.setManager(true);
    UserProfile updated = new UserProfile();
    updated.setIsManager(true);
    when(userService.setManagerStatus(externalId, true)).thenReturn(updated);

    UserProfile result = controller.updateManagerStatus(externalId, request);

    assertSame(updated, result);
    verify(userService).setManagerStatus(externalId, true);
  }

  @Test
  void patchManagerStatusNotFoundMapsTo404() {
    UUID externalId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    ManagerStatusUpdateRequest request = new ManagerStatusUpdateRequest();
    request.setManager(false);
    when(userService.setManagerStatus(externalId, false)).thenThrow(new UserNotFoundException("User not found"));

    assertThrows(UserNotFoundException.class,
        () -> controller.updateManagerStatus(externalId, request));
    verify(userService).setManagerStatus(externalId, false);
  }

  @Test
  void patchEnabledPassesBooleanThroughAndReturnsUpdatedUser() {
    UUID externalId = UUID.fromString("55555555-5555-5555-5555-555555555555");
    UserEnabledUpdateRequest request = new UserEnabledUpdateRequest();
    request.setEnabled(false);
    UserProfile updated = new UserProfile();
    updated.setUserEnabled(false);
    when(userService.setEnabled(externalId, false)).thenReturn(updated);

    UserProfile result = controller.updateEnabled(externalId, request);

    assertSame(updated, result);
    verify(userService).setEnabled(externalId, false);
  }

  @Test
  void patchEnabledNotFoundMapsTo404() {
    UUID externalId = UUID.fromString("66666666-6666-6666-6666-666666666666");
    UserEnabledUpdateRequest request = new UserEnabledUpdateRequest();
    request.setEnabled(true);
    when(userService.setEnabled(externalId, true)).thenThrow(new UserNotFoundException("User not found"));

    assertThrows(UserNotFoundException.class,
        () -> controller.updateEnabled(externalId, request));
    verify(userService).setEnabled(externalId, true);
  }

}

