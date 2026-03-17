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
//package edu.cnm.deepdive.seesomethingabq.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
//import edu.cnm.deepdive.seesomethingabq.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.oauth2.jwt.Jwt;
//
///**
// * Unit tests for {@link UserController}.
// */
//@ExtendWith(MockitoExtension.class)
//class UserControllerTest {
//
//  @Mock
//  private UserService userService;
//
//
//  private UserController controller;
//  private UserProfile testUser;
//
//  @BeforeEach
//  void setUp() {
//    controller = new UserController(userService);
//
//    testUser = new UserProfile();
//    testUser.setOauthKey("test-oauth-key");
//    testUser.setDisplayName("Test User");
//  }
//
//  @Test
//  void testGetCurrentUser() {
//    when(userService.getCurrentUser()).thenReturn(testUser);
//
//    UserProfile result = userService.getCurrentUser();
//
//    assertNotNull(result);
//    assertEquals("test-oauth-key", result.getOauthKey());
//    assertEquals("Test User", result.getDisplayName());
//  }
//
////  @Test
////  void testUpdateCurrentUser() throws Exception {
////    // Use reflection to set the ID field since it has no setter
////    java.lang.reflect.Field idField = UserProfile.class.getDeclaredField("id");
////    idField.setAccessible(true);
////    idField.set(testUser, 1L);
////
////    UserProfile updatedUser = new UserProfile();
////    updatedUser.setOauthKey("test-oauth-key");
////    updatedUser.setDisplayName("Updated Name");
////
////    UserProfile requestBody = new UserProfile();
////    requestBody.setDisplayName("Updated Name");
////
////    when(userService.getCurrentUser()).thenReturn(testUser);
////    when(userService.updateDisplayName(1L, "Updated Name")).thenReturn(updatedUser);
////
////    UserProfile result = userService.updateCurrentUser(requestBody);
////
////    assertNotNull(result);
////    assertEquals("Updated Name", result.getDisplayName());
////  }
//
//}
