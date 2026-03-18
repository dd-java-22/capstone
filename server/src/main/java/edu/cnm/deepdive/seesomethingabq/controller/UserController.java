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

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user profile operations. This controller exposes endpoints for retrieving and
 * updating user profile information. All operations require authentication.
 */
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService service;

  /**
   * Constructs an instance of {@code UserController} with the specified service.
   *
   * @param service User service for business logic operations.
   */
  @Autowired
  public UserController(UserService service) {
    this.service = service;
  }

  /**
   * Returns the user profile for the currently authenticated user. If the user does not already
   * exist in the system, a new profile will be created automatically.
   *
   * @return User profile for the authenticated user.
   */

  @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
  public UserProfile get() {
    return service.getMe();
  }

  @GetMapping
  public List<UserProfile> getAll() {
    return service.getAll();
  }
  /*
   */
/**
 * Updates the display name for the currently authenticated user.
 *
 * @param jwt JWT token from the authenticated request.
 * @param updated User profile containing the updated display name.
 * @return Updated user profile.
 *//*

  @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public UserProfile updateCurrentUser(
      @AuthenticationPrincipal Jwt jwt,
      @RequestBody UserProfile updated) {
    UserProfile current = service.getCurrentUser();
    return service.updateDisplayName(current.getId(), updated.getDisplayName());
  }
*/

}
