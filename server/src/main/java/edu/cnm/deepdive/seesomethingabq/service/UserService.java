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

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.util.Optional;
import org.springframework.security.oauth2.jwt.Jwt;

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
   * @param jwt JWT token from the current authenticated request.
   * @return User profile for the authenticated user.
   */
  UserProfile getCurrentUser(Jwt jwt);

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
   * @param displayName Optional display name for new user profiles.
   * @return User profile for the given OAuth2 key.
   */
  UserProfile getOrCreate(String oauthKey, String displayName, String email);

  /**
   * Updates the display name for the specified user profile.
   *
   * @param id User profile ID.
   * @param displayName New display name.
   * @return Updated user profile.
   */
  UserProfile updateDisplayName(Long id, String displayName);

}
