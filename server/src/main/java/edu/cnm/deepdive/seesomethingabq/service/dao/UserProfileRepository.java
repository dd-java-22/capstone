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
package edu.cnm.deepdive.seesomethingabq.service.dao;

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link UserProfile} entity persistence operations.
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

  /**
   * Finds a user profile by OAuth2 key.
   *
   * @param oauthKey OAuth2 key to search for.
   * @return {@link Optional} containing the user profile if found, empty otherwise.
   */
  Optional<UserProfile> findByOauthKey(String oauthKey);

}
