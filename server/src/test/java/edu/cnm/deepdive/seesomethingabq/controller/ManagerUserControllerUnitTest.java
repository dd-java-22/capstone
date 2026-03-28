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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.seesomethingabq.model.dto.ManagerStatusUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.ManagerUserResponse;
import edu.cnm.deepdive.seesomethingabq.model.dto.UserEnabledUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.UserService;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

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
  void getAllReturnsMappedListFromService() throws MalformedURLException {
    UserProfile first = buildUser(
        UUID.fromString("11111111-1111-1111-1111-111111111111"),
        "First User",
        "first@example.com",
        new URL("https://example.com/avatars/first.png"),
        true,
        Instant.parse("2026-03-01T10:15:30Z"),
        true
    );
    UserProfile second = buildUser(
        UUID.fromString("22222222-2222-2222-2222-222222222222"),
        "Second User",
        "second@example.com",
        new URL("https://example.com/avatars/second.png"),
        false,
        Instant.parse("2026-03-02T10:15:30Z"),
        false
    );
    List<UserProfile> users = List.of(first, second);
    when(userService.getAll()).thenReturn(users);

    List<ManagerUserResponse> result = controller.getAll();

    assertNotNull(result);
    assertEquals(2, result.size());
    assertUserResponseMatches(first, result.get(0));
    assertUserResponseMatches(second, result.get(1));
    verify(userService).getAll();
  }

  @Test
  void getByExternalIdReturnsRequestedUser() throws MalformedURLException {
    UUID externalId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    UserProfile user = buildUser(
        externalId,
        "User",
        "user@example.com",
        new URL("https://example.com/avatars/user.png"),
        false,
        Instant.parse("2026-03-03T10:15:30Z"),
        true
    );
    when(userService.getByExternalId(externalId)).thenReturn(Optional.of(user));

    ManagerUserResponse result = controller.get(externalId);

    assertNotNull(result);
    assertUserResponseMatches(user, result);
    verify(userService).getByExternalId(externalId);
  }

  @Test
  void getByExternalIdNotFoundMapsTo404() {
    UUID externalId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(userService.getByExternalId(externalId)).thenReturn(Optional.empty());

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> controller.get(externalId));
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    verify(userService).getByExternalId(externalId);
  }

  @Test
  void patchManagerStatusPassesBooleanThroughAndReturnsUpdatedUser() throws MalformedURLException {
    UUID externalId = UUID.fromString("33333333-3333-3333-3333-333333333333");
    ManagerStatusUpdateRequest request = new ManagerStatusUpdateRequest();
    request.setManager(true);
    UserProfile updated = buildUser(
        externalId,
        "Manager User",
        "manager@example.com",
        new URL("https://example.com/avatars/manager.png"),
        true,
        Instant.parse("2026-03-04T10:15:30Z"),
        true
    );
    when(userService.setManagerStatus(externalId, true)).thenReturn(updated);

    ManagerUserResponse result = controller.updateManagerStatus(externalId, request);

    assertUserResponseMatches(updated, result);
    verify(userService).setManagerStatus(externalId, true);
  }

  @Test
  void patchManagerStatusNotFoundMapsTo404() {
    UUID externalId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    ManagerStatusUpdateRequest request = new ManagerStatusUpdateRequest();
    request.setManager(false);
    when(userService.setManagerStatus(externalId, false)).thenThrow(new IllegalArgumentException());

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> controller.updateManagerStatus(externalId, request));
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    verify(userService).setManagerStatus(externalId, false);
  }

  @Test
  void patchEnabledPassesBooleanThroughAndReturnsUpdatedUser() throws MalformedURLException {
    UUID externalId = UUID.fromString("55555555-5555-5555-5555-555555555555");
    UserEnabledUpdateRequest request = new UserEnabledUpdateRequest();
    request.setEnabled(false);
    UserProfile updated = buildUser(
        externalId,
        "Disabled User",
        "disabled@example.com",
        new URL("https://example.com/avatars/disabled.png"),
        false,
        Instant.parse("2026-03-05T10:15:30Z"),
        false
    );
    when(userService.setEnabled(externalId, false)).thenReturn(updated);

    ManagerUserResponse result = controller.updateEnabled(externalId, request);

    assertUserResponseMatches(updated, result);
    verify(userService).setEnabled(externalId, false);
  }

  @Test
  void patchEnabledNotFoundMapsTo404() {
    UUID externalId = UUID.fromString("66666666-6666-6666-6666-666666666666");
    UserEnabledUpdateRequest request = new UserEnabledUpdateRequest();
    request.setEnabled(true);
    when(userService.setEnabled(externalId, true)).thenThrow(new IllegalArgumentException());

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> controller.updateEnabled(externalId, request));
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    verify(userService).setEnabled(externalId, true);
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

  private static void assertUserResponseMatches(UserProfile userProfile,
      ManagerUserResponse response) {
    assertNotNull(response);
    assertEquals(userProfile.getExternalId(), response.getExternalId());
    assertEquals(userProfile.getDisplayName(), response.getDisplayName());
    assertEquals(userProfile.getEmail(), response.getEmail());
    assertEquals(userProfile.getAvatar(), response.getAvatar());
    assertEquals(userProfile.isManager(), response.isManager());
    assertEquals(userProfile.getTimeCreated(), response.getTimeCreated());
    assertEquals(userProfile.getUserEnabled(), response.isUserEnabled());
    assertFalse(hasField(response, "id"));
    assertFalse(hasField(response, "oauthKey"));
    assertFalse(hasField(response, "issueReports"));
  }

  private static boolean hasField(Object target, String fieldName) {
    return ReflectionUtils.findField(target.getClass(), fieldName) != null;
  }

}

