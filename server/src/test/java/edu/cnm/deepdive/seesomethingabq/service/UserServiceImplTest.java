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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.UserProfileRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link UserServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserProfileRepository repository;

  @InjectMocks
  private UserServiceImpl service;

  private UserProfile testUser;

  @BeforeEach
  void setUp() {
    testUser = new UserProfile();
    testUser.setOauthKey("test-oauth-key");
    testUser.setDisplayName("Test User");
    testUser.setEmail("test@example.com");
  }

  @Test
  void testGetExistingUser() {
    when(repository.findById(1L)).thenReturn(Optional.of(testUser));

    Optional<UserProfile> result = service.get(1L);

    assertTrue(result.isPresent());
    assertEquals("test-oauth-key", result.get().getOauthKey());
  }

  @Test
  void testGetNonExistentUser() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    Optional<UserProfile> result = service.get(999L);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetOrCreateExistingUser() {
    when(repository.findByOauthKey("existing-key")).thenReturn(Optional.of(testUser));

    UserProfile result = service.getOrCreate("existing-key", new UserProfile());

    assertNotNull(result);
    assertEquals("test-oauth-key", result.getOauthKey());
    verify(repository, never()).save(any(UserProfile.class));
  }

  @Test
  void testGetOrCreateNewUser() {
    UserProfile newUser = new UserProfile();
    newUser.setDisplayName("New User");
    newUser.setEmail("new@example.com");

    when(repository.findByOauthKey("new-key")).thenReturn(Optional.empty());
    when(repository.save(any(UserProfile.class))).thenAnswer(invocation -> {
      UserProfile user = invocation.getArgument(0);
      return user;
    });

    UserProfile result = service.getOrCreate("new-key", newUser);

    assertNotNull(result);
    assertEquals("new-key", result.getOauthKey());
    verify(repository, times(1)).save(any(UserProfile.class));
  }

  @Test
  void testUpdateDisplayName() {
    when(repository.findById(1L)).thenReturn(Optional.of(testUser));
    when(repository.save(any(UserProfile.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    UserProfile result = service.updateDisplayName(1L, "Updated Name");

    assertNotNull(result);
    assertEquals("Updated Name", result.getDisplayName());
    verify(repository, times(1)).save(testUser);
  }

  @Test
  void testUpdateDisplayNameUserNotFound() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> {
      service.updateDisplayName(999L, "Updated Name");
    });

    verify(repository, never()).save(any(UserProfile.class));
  }

  @Test
  void testUpdateEmail() {
    when(repository.findById(1L)).thenReturn(Optional.of(testUser));
    when(repository.save(any(UserProfile.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    UserProfile result = service.updateEmail(1L, "updated@example.com");

    assertNotNull(result);
    assertEquals("updated@example.com", result.getEmail());
    verify(repository, times(1)).save(testUser);
  }

  @Test
  void testUpdateEmailUserNotFound() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> {
      service.updateEmail(999L, "updated@example.com");
    });

    verify(repository, never()).save(any(UserProfile.class));
  }

  @Test
  void testUpdateAvatar() throws Exception {
    java.net.URL avatarUrl = new java.net.URL("https://example.com/avatar.jpg");
    when(repository.findById(1L)).thenReturn(Optional.of(testUser));
    when(repository.save(any(UserProfile.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    UserProfile result = service.updateAvatar(1L, avatarUrl);

    assertNotNull(result);
    assertEquals(avatarUrl, result.getAvatar());
    verify(repository, times(1)).save(testUser);
  }

  @Test
  void testUpdateAvatarUserNotFound() throws Exception {
    java.net.URL avatarUrl = new java.net.URL("https://example.com/avatar.jpg");
    when(repository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> {
      service.updateAvatar(999L, avatarUrl);
    });

    verify(repository, never()).save(any(UserProfile.class));
  }

  @Test
  void testGetByExternalIdFound() {
    UUID externalId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    when(repository.findByExternalId(externalId)).thenReturn(Optional.of(testUser));

    Optional<UserProfile> result = service.getByExternalId(externalId);

    assertTrue(result.isPresent());
    assertEquals("test-oauth-key", result.get().getOauthKey());
    verify(repository, times(1)).findByExternalId(externalId);
  }

  @Test
  void testGetByExternalIdNotFound() {
    UUID externalId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    when(repository.findByExternalId(externalId)).thenReturn(Optional.empty());

    Optional<UserProfile> result = service.getByExternalId(externalId);

    assertTrue(result.isEmpty());
    verify(repository, times(1)).findByExternalId(externalId);
  }

  @Test
  void testSetManagerStatusFound() {
    UUID externalId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    testUser.setIsManager(false);
    when(repository.findByExternalId(externalId)).thenReturn(Optional.of(testUser));
    when(repository.save(any(UserProfile.class))).thenAnswer(
        invocation -> invocation.getArgument(0));
    UserProfile result = service.setManagerStatus(externalId, true);

    assertNotNull(result);
    assertTrue(result.isManager());
    verify(repository, times(1)).findByExternalId(externalId);
    verify(repository, times(1)).save(testUser);
  }

  @Test
  void testSetManagerStatusNotFound() {
    UUID externalId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    when(repository.findByExternalId(externalId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> service.setManagerStatus(externalId, true));

    verify(repository, times(1)).findByExternalId(externalId);
    verify(repository, never()).save(any(UserProfile.class));
  }

  @Test
  void testSetEnabledFound() {
    UUID externalId = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
    testUser.setUserEnabled(true);
    when(repository.findByExternalId(externalId)).thenReturn(Optional.of(testUser));
    when(repository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

    UserProfile result = service.setEnabled(externalId, false);

    assertNotNull(result);
    assertEquals(false, result.getUserEnabled());
    verify(repository, times(1)).findByExternalId(externalId);
    verify(repository, times(1)).save(testUser);
  }

  @Test
  void testSetEnabledNotFound() {
    UUID externalId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    when(repository.findByExternalId(externalId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> service.setEnabled(externalId, true));

    verify(repository, times(1)).findByExternalId(externalId);
    verify(repository, never()).save(any(UserProfile.class));
  }

}





