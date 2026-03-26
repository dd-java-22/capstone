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

import edu.cnm.deepdive.seesomethingabq.model.dto.ManagerStatusUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.UserEnabledUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for manager-only user administration operations.
 */
@RestController
@RequestMapping("/manager/users")
public class ManagerController {

  private final UserService service;

  @Autowired
  public ManagerController(UserService service) {
    this.service = service;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<UserProfile> getAll() {
    return service.getAll();
  }

  @GetMapping(value = "/{externalId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public UserProfile get(@PathVariable UUID externalId) {
    return service
        .getByExternalId(externalId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @PatchMapping(
      value = "/{externalId}/manager-status",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public UserProfile updateManagerStatus(
      @PathVariable UUID externalId,
      @RequestBody ManagerStatusUpdateRequest request
  ) {
    try {
      return service.setManagerStatus(externalId, request.isManager());
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, ex);
    }
  }

  @PatchMapping(
      value = "/{externalId}/enabled",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public UserProfile updateEnabled(
      @PathVariable UUID externalId,
      @RequestBody UserEnabledUpdateRequest request
  ) {
    try {
      return service.setEnabled(externalId, request.isEnabled());
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, ex);
    }
  }

}

