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
//package edu.cnm.deepdive.seesomethingabq.service.dao;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
//import java.util.Optional;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * Integration tests for {@link UserProfileRepository}.
// */
//@SpringBootTest
//@Transactional
//class UserProfileRepositoryTest {
//
//  @Autowired
//  private UserProfileRepository repository;
//
//  @Test
//  void testSaveAndFindById() {
//    UserProfile user = new UserProfile();
//    user.setOauthKey("oauth-key-123");
//    user.setDisplayName("Test User");
//
//    UserProfile saved = repository.save(user);
//
//    Optional<UserProfile> found = repository.findById(saved.getId());
//
//    assertTrue(found.isPresent());
//    assertEquals("oauth-key-123", found.get().getOauthKey());
//    assertEquals("Test User", found.get().getDisplayName());
//  }
//
//  @Test
//  void testFindByOauthKey() {
//    UserProfile user = new UserProfile();
//    user.setOauthKey("oauth-key-456");
//    user.setDisplayName("Another User");
//
//    repository.save(user);
//
//    Optional<UserProfile> found = repository.findByOauthKey("oauth-key-456");
//
//    assertTrue(found.isPresent());
//    assertEquals("oauth-key-456", found.get().getOauthKey());
//    assertEquals("Another User", found.get().getDisplayName());
//  }
//
//  @Test
//  void testFindByOauthKeyNotFound() {
//    Optional<UserProfile> found = repository.findByOauthKey("non-existent-key");
//
//    assertTrue(found.isEmpty());
//  }
//
//  @Test
//  void testCreatedTimestampIsSet() {
//    UserProfile user = new UserProfile();
//    user.setOauthKey("oauth-key-789");
//
//    UserProfile saved = repository.save(user);
//
//    assertNotNull(saved.getTimeCreated());
//  }
//
//  @Test
//  void testOauthKeyUniqueness() {
//    UserProfile user1 = new UserProfile();
//    user1.setOauthKey("duplicate-key-test");
//    user1.setDisplayName("User 1");
//
//    repository.save(user1);
//    repository.flush();
//
//    UserProfile user2 = new UserProfile();
//    user2.setOauthKey("duplicate-key-test");
//    user2.setDisplayName("User 2");
//
//    assertThrows(DataIntegrityViolationException.class, () -> {
//      repository.save(user2);
//      repository.flush();
//    });
//  }
//
//}
