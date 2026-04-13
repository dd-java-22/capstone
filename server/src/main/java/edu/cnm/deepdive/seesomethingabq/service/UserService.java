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

import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileResponse;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for user profile business logic operations. Implementations of this interface
 * provide methods for retrieving and managing user profiles, including OAuth2-based user
 * identification.
 */
public interface UserService {

  /**
   * Returns the user profile associated with the current authenticated request. If the user does
   * not already exist in the system, a new user profile will be created.
   *
   * @return User profile for the authenticated user.
   */
  UserProfile getCurrentUser();

  /**
   * Returns the user profile with the specified ID, if it exists.
   *
   * @param id User profile ID.
   * @return {@link Optional} containing the user profile if found, empty otherwise.
   */
  Optional<UserProfile> get(Long id);

  /**
   * Returns the user profile for the specified OAuth2 key, creating a new profile if one does not
   * already exist.
   *
   * @param oauthKey OAuth2 key (typically the "sub" claim from the JWT).
   * @param userProfile Optional display name for new user profiles.
   * @return User profile for the given OAuth2 key.
   */
  UserProfile getOrCreate(String oauthKey, UserProfile userProfile);

  /**
   * Updates the display name for the specified user profile.
   *
   * @param id User profile ID.
   * @param displayName New display name.
   * @return Updated user profile.
   */
  UserProfile updateDisplayName(Long id, String displayName);

  /**
   * Updates the email address for the specified user profile.
   *
   * @param id User profile ID.
   * @param email New email address.
   * @return Updated user profile.
   */
  UserProfile updateEmail(Long id, String email);

  /**
   * Updates the avatar URL for the specified user profile.
   *
   * @param id User profile ID.
   * @param avatar New avatar URL.
   * @return Updated user profile.
   */
  UserProfile updateAvatar(Long id, URL avatar);

  /**
   * Updates the avatar for the specified user profile using a storage key.
   * The storage key is converted to a URL using the configured base URL.
   *
   * @param id User profile ID.
   * @param storageKey Storage key for the uploaded avatar image.
   * @return Updated user profile.
   */
  UserProfile updateAvatarKey(Long id, String storageKey);

  /**
   * Returns all user profiles.
   *
   * @return list of user profiles.
   */
  List<UserProfile> getAll();

  /**
   * Returns a page of user profiles.
   *
   * @param pageable paging information.
   * @return page of user profiles.
   */
  Page<UserProfile> getAll(Pageable pageable);

  /**
   * Returns the user profile for the currently authenticated user, creating one if needed.
   *
   * @return current user's profile.
   */
  UserProfile getMe();

  /**
   * Finds a user profile by external identifier.
   *
   * @param externalId user external ID.
   * @return optional containing the user profile if found.
   */
  Optional<UserProfile> getByExternalId(UUID externalId);

  /**
   * Sets manager privilege for a user profile.
   *
   * @param externalId user external ID.
   * @param manager {@code true} to grant manager privileges; {@code false} to revoke.
   * @return updated user profile.
   */
  UserProfile setManagerStatus(UUID externalId, boolean manager);

  /**
   * Sets enabled/disabled status for a user profile.
   *
   * @param externalId user external ID.
   * @param enabled {@code true} to enable; {@code false} to disable.
   * @return updated user profile.
   */
  UserProfile setEnabled(UUID externalId, boolean enabled);

  /**
   * Converts a user profile entity to a response DTO enriched with report count.
   *
   * @param userProfile source user profile.
   * @return enriched user profile response.
   */
  UserProfileResponse getUserProfileResponse(UserProfile userProfile);

}
