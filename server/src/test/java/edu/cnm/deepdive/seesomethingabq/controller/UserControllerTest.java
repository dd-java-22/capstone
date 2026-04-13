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
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileResponse;
import edu.cnm.deepdive.seesomethingabq.model.dto.UpdateAvatarRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.UpdateDisplayNameRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.UpdateEmailRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.UserService;
import edu.cnm.deepdive.seesomethingabq.service.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link UserController}.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  private UserService userService;

  @Mock
  private StorageService storageService;

  private UserController controller;
  private UserProfile testUser;

  @BeforeEach
  void setUp() {
    controller = new UserController(userService, storageService);

    testUser = new UserProfile();
    testUser.setOauthKey("test-oauth-key");
    testUser.setDisplayName("Test User");
    testUser.setEmail("test@example.com");
  }

  @Test
  void testGetMe() {
    when(userService.getMe()).thenReturn(testUser);

    UserProfileResponse response = new UserProfileResponse(
        testUser.getExternalId(),
        testUser.getDisplayName(),
        testUser.getEmail(),
        testUser.getAvatar(),
        testUser.isManager(),
        testUser.getTimeCreated(),
        testUser.getUserEnabled(),
        0
    );
    when(userService.getUserProfileResponse(testUser)).thenReturn(response);

    UserProfileResponse result = controller.get();

    assertNotNull(result);
    assertEquals("Test User", result.displayName());
  }

  @Test
  void testUpdateDisplayName() {
    UpdateDisplayNameRequest request = new UpdateDisplayNameRequest();
    request.setDisplayName("Updated Display Name");

    UserProfile updatedUser = new UserProfile();
    updatedUser.setOauthKey("test-oauth-key");
    updatedUser.setDisplayName("Updated Display Name");
    updatedUser.setEmail("test@example.com");

    when(userService.getCurrentUser()).thenReturn(testUser);
    when(userService.updateDisplayName(null, "Updated Display Name")).thenReturn(updatedUser);

    UserProfile result = controller.updateDisplayName(request);

    assertNotNull(result);
    assertEquals("Updated Display Name", result.getDisplayName());
  }

  @Test
  void testUpdateEmail() {
    UpdateEmailRequest request = new UpdateEmailRequest();
    request.setEmail("updated@example.com");

    UserProfile updatedUser = new UserProfile();
    updatedUser.setOauthKey("test-oauth-key");
    updatedUser.setDisplayName("Test User");
    updatedUser.setEmail("updated@example.com");

    when(userService.getCurrentUser()).thenReturn(testUser);
    when(userService.updateEmail(null, "updated@example.com")).thenReturn(updatedUser);

    UserProfile result = controller.updateEmail(request);

    assertNotNull(result);
    assertEquals("updated@example.com", result.getEmail());
  }

  @Test
  void testUpdateAvatar() throws Exception {
    java.net.URL avatarUrl = new java.net.URL("https://example.com/avatar.jpg");
    UpdateAvatarRequest request = new UpdateAvatarRequest();
    request.setAvatar(avatarUrl);

    UserProfile updatedUser = new UserProfile();
    updatedUser.setOauthKey("test-oauth-key");
    updatedUser.setDisplayName("Test User");
    updatedUser.setEmail("test@example.com");
    updatedUser.setAvatar(avatarUrl);

    when(userService.getCurrentUser()).thenReturn(testUser);
    when(userService.updateAvatar(null, avatarUrl)).thenReturn(updatedUser);

    UserProfile result = controller.updateAvatar(request);

    assertNotNull(result);
    assertEquals(avatarUrl, result.getAvatar());
  }

}
