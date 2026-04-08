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

import edu.cnm.deepdive.seesomethingabq.exception.UserNotFoundException;
import edu.cnm.deepdive.seesomethingabq.model.dto.ManagerStatusUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.UserEnabledUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for manager-only user administration operations.
 */
@RestController
@RequestMapping("/manager/users")
public class ManagerUserController {

  private final UserService service;

  /**
   * Creates a controller exposing manager-only user administration operations.
   *
   * @param service user service.
   */
  @Autowired
  public ManagerUserController(UserService service) {
    this.service = service;
  }

  /**
   * Returns all user profiles.
   *
   * @return list of user profiles.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<UserProfile> getAll() {
    return service.getAll();
  }

  /**
   * Returns a user profile by external identifier.
   *
   * @param externalId user external ID.
   * @return user profile.
   * @throws UserNotFoundException if no user exists with {@code externalId}.
   */
  @GetMapping(value = "/{externalId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public UserProfile get(@PathVariable UUID externalId) {
    return service
        .getByExternalId(externalId)
        .orElseThrow(() -> new UserNotFoundException("User not found: " + externalId));
  }

  /**
   * Sets manager status for a user profile.
   *
   * @param externalId user external ID.
   * @param request request payload containing desired manager flag.
   * @return updated user profile.
   */
  @PatchMapping(
      value = "/{externalId}/manager-status",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public UserProfile updateManagerStatus(
      @PathVariable UUID externalId,
      @RequestBody ManagerStatusUpdateRequest request
  ) {
    return service.setManagerStatus(externalId, request.isManager());
  }

  /**
   * Sets enabled/disabled status for a user profile.
   *
   * @param externalId user external ID.
   * @param request request payload containing desired enabled flag.
   * @return updated user profile.
   */
  @PatchMapping(
      value = "/{externalId}/enabled",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public UserProfile updateEnabled(
      @PathVariable UUID externalId,
      @RequestBody UserEnabledUpdateRequest request
  ) {
    return service.setEnabled(externalId, request.isEnabled());
  }

}

