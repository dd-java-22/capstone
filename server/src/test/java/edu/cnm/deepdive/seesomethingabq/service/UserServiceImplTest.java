///*
// *  Copyright 2026 CNM Ingenuity, Inc.
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//package edu.cnm.deepdive.seesomethingabq.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
//import edu.cnm.deepdive.seesomethingabq.service.dao.UserProfileRepository;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.oauth2.jwt.Jwt;
//
///**
// * Unit tests for {@link UserServiceImpl}.
// */
//@ExtendWith(MockitoExtension.class)
//class UserServiceImplTest {
//
//  @Mock
//  private UserProfileRepository repository;
//
//  @Mock
//  private Jwt jwt;
//
//  @InjectMocks
//  private UserServiceImpl service;
//
//  private UserProfile testUser;
//
//  @BeforeEach
//  void setUp() {
//    testUser = new UserProfile();
//    testUser.setOauthKey("test-oauth-key");
//    testUser.setDisplayName("Test User");
//  }
//
//  @Test
//  void testGetCurrentUserExistingUser() {
//    when(jwt.getSubject()).thenReturn("test-oauth-key");
//    when(jwt.getClaimAsString("name")).thenReturn("Test User");
//    when(repository.findByOauthKey("test-oauth-key")).thenReturn(Optional.of(testUser));
//
//    UserProfile result = service.getCurrentUser();
//
//    assertNotNull(result);
//    assertEquals("test-oauth-key", result.getOauthKey());
//    assertEquals("Test User", result.getDisplayName());
//    verify(repository, times(1)).findByOauthKey("test-oauth-key");
//    verify(repository, never()).save(any(UserProfile.class));
//  }
//
//  @Test
//  void testGetCurrentUserNewUser() {
//    when(jwt.getSubject()).thenReturn("new-oauth-key");
//    when(jwt.getClaimAsString("name")).thenReturn("New User");
//    when(repository.findByOauthKey("new-oauth-key")).thenReturn(Optional.empty());
//    when(repository.save(any(UserProfile.class))).thenAnswer(invocation -> {
//      UserProfile user = invocation.getArgument(0);
//      return user;
//    });
//
//    UserProfile result = service.getCurrentUser();
//
//    assertNotNull(result);
//    assertEquals("new-oauth-key", result.getOauthKey());
//    assertEquals("New User", result.getDisplayName());
//    verify(repository, times(1)).findByOauthKey("new-oauth-key");
//    verify(repository, times(1)).save(any(UserProfile.class));
//  }
//
//  @Test
//  void testGetExistingUser() {
//    when(repository.findById(1L)).thenReturn(Optional.of(testUser));
//
//    Optional<UserProfile> result = service.get(1L);
//
//    assertTrue(result.isPresent());
//    assertEquals("test-oauth-key", result.get().getOauthKey());
//  }
//
//  @Test
//  void testGetNonExistentUser() {
//    when(repository.findById(999L)).thenReturn(Optional.empty());
//
//    Optional<UserProfile> result = service.get(999L);
//
//    assertTrue(result.isEmpty());
//  }
//
//  @Test
//  void testGetOrCreateExistingUser() {
//    when(repository.findByOauthKey("existing-key")).thenReturn(Optional.of(testUser));
//
//    UserProfile result = service.getOrCreate("existing-key", new UserProfile());
//
//    assertNotNull(result);
//    assertEquals("test-oauth-key", result.getOauthKey());
//    verify(repository, never()).save(any(UserProfile.class));
//  }
//
//  @Test
//  void testGetOrCreateNewUser() {
//    when(repository.findByOauthKey("new-key")).thenReturn(Optional.empty());
//    when(repository.save(any(UserProfile.class))).thenAnswer(invocation -> {
//      UserProfile user = invocation.getArgument(0);
//      return user;
//    });
//
//    UserProfile result = service.getOrCreate("new-key", new UserProfile());
//
//    assertNotNull(result);
//    assertEquals("new-key", result.getOauthKey());
//    assertEquals("New Display Name", result.getDisplayName());
//    verify(repository, times(1)).save(any(UserProfile.class));
//  }
//
//  @Test
//  void testUpdateDisplayName() {
//    when(repository.findById(1L)).thenReturn(Optional.of(testUser));
//    when(repository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//    UserProfile result = service.updateDisplayName(1L, "Updated Name");
//
//    assertNotNull(result);
//    assertEquals("Updated Name", result.getDisplayName());
//    verify(repository, times(1)).save(testUser);
//  }
//
//  @Test
//  void testUpdateDisplayNameUserNotFound() {
//    when(repository.findById(999L)).thenReturn(Optional.empty());
//
//    assertThrows(NoSuchElementException.class, () -> {
//      service.updateDisplayName(999L, "Updated Name");
//    });
//
//    verify(repository, never()).save(any(UserProfile.class));
//  }
//
//}
