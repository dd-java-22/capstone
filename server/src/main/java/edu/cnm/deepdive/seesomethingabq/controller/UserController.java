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

import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileResponse;
import edu.cnm.deepdive.seesomethingabq.model.dto.UpdateAvatarRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.UpdateDisplayNameRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.UpdateEmailRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.UpdateUserRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.UserService;
import edu.cnm.deepdive.seesomethingabq.service.storage.StorageService;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for user profile operations. This controller exposes endpoints for retrieving and
 * updating user profile information. All operations require authentication.
 */
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService service;
  private final StorageService storageService;

  /**
   * Constructs an instance of {@code UserController} with the specified services.
   *
   * @param service User service for business logic operations.
   * @param storageService Storage service for file uploads.
   */
  @Autowired
  public UserController(UserService service, StorageService storageService) {
    this.service = service;
    this.storageService = storageService;
  }

  /**
   * Returns the user profile for the currently authenticated user. If the user does not already
   * exist in the system, a new profile will be created automatically.
   *
   * @return User profile for the authenticated user.
   */
  @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
  public UserProfileResponse get() {
    UserProfile current = service.getMe();
    return service.getUserProfileResponse(current);
  }

  /**
   * Updates the display name for the currently authenticated user.
   *
   * @param request Request containing the new display name.
   * @return Updated user profile.
   */
  @PutMapping(value = "/me/display-name", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public UserProfile updateDisplayName(@RequestBody UpdateDisplayNameRequest request) {
    UserProfile current = service.getCurrentUser();
    return service.updateDisplayName(current.getId(), request.getDisplayName());
  }

  /**
   * Updates the email address for the currently authenticated user.
   *
   * @param request Request containing the new email address.
   * @return Updated user profile.
   */
  @PutMapping(value = "/me/email", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public UserProfile updateEmail(@RequestBody UpdateEmailRequest request) {
    UserProfile current = service.getCurrentUser();
    return service.updateEmail(current.getId(), request.getEmail());
  }

  /**
   * Updates the avatar URL for the currently authenticated user.
   *
   * @param request Request containing the new avatar URL.
   * @return Updated user profile.
   */
  @PutMapping(value = "/me/avatar", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public UserProfile updateAvatar(@RequestBody UpdateAvatarRequest request) {
    UserProfile current = service.getCurrentUser();
    return service.updateAvatar(current.getId(), request.getAvatar());
  }

  /**
   * Updates user profile fields (display name and/or email) in a single request.
   *
   * @param request Request containing the fields to update (nullable fields are ignored).
   * @return Updated user profile.
   */
  @PatchMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public UserProfileResponse updateProfile(@RequestBody UpdateUserRequest request) {
    UserProfile current = service.getCurrentUser();
    UserProfile updated = current;

    if (request.getDisplayName() != null) {
      updated = service.updateDisplayName(current.getId(), request.getDisplayName());
    }

    if (request.getEmail() != null) {
      updated = service.updateEmail(updated.getId(), request.getEmail());
    }

    return service.getUserProfileResponse(updated);
  }

  /**
   * Uploads a new avatar image for the currently authenticated user.
   * The uploaded file is stored and the user's avatar URL is updated to point to the dedicated
   * backend-served avatar endpoint.
   *
   * @param avatar The avatar image file to upload (preferred part name).
   * @param file Alternate part name supported for consistency with other upload endpoints.
   * @return Updated user profile with new avatar URL.
   * @throws IOException If file upload fails.
   * @throws HttpMediaTypeException If the file type is not supported.
   */
  @RequestMapping(
      value = "/me/avatar",
      method = {RequestMethod.POST, RequestMethod.PUT},
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public UserProfile uploadAvatarImage(
      @RequestPart(value = "avatar", required = false) MultipartFile avatar,
      @RequestPart(value = "file", required = false) MultipartFile file
  )
      throws IOException, HttpMediaTypeException {
    UserProfile current = service.getCurrentUser();
    MultipartFile selected = (avatar != null) ? avatar : file;
    if (selected == null) {
      throw new IllegalArgumentException("Missing avatar file upload.");
    }
    String storageKey = storageService.store(selected);
    String contentType = (selected.getContentType() != null)
        ? selected.getContentType()
        : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    return service.updateAvatarKey(current.getId(), storageKey, contentType);
  }

  /**
   * Serves a user's custom avatar (if present) as raw bytes.
   *
   * @param externalId user external ID.
   * @return avatar content as a {@link Resource}.
   * @throws IOException if storage retrieval fails.
   */
  @GetMapping(value = "/{externalId}/avatar", produces = MediaType.ALL_VALUE)
  public ResponseEntity<Resource> getAvatar(@PathVariable UUID externalId) throws IOException {
    UserProfile user = service.getByExternalId(externalId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + externalId));

    String key = user.getAvatarKey();
    if (key == null || key.isBlank()) {
      // No custom avatar stored; clients should use the user's avatar URL (e.g., Google picture URL).
      return ResponseEntity.notFound().build();
    }

    Resource resource = storageService.retrieve(key);
    String contentType = (user.getAvatarMimeType() != null && !user.getAvatarMimeType().isBlank())
        ? user.getAvatarMimeType()
        : MediaType.APPLICATION_OCTET_STREAM_VALUE;

    return ResponseEntity
        .ok()
        .contentType(MediaType.parseMediaType(contentType))
        .body(resource);
  }

}
