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
package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import org.springframework.lang.NonNull;

/**
 * Represents a user profile in the See Something, Say Something application. Each user is uniquely
 * identified by their OAuth2 key and can have an optional display name.
 */
@Entity
@Table(
    name = "user_profile",
    indexes = {
        @Index(columnList = "oauth_key", unique = true)
    }
)
public class UserProfile {

  @Id
  @GeneratedValue
  @Column(name = "user_profile_id", updatable = false, nullable = false)
  private Long id;

  @NonNull
  @Column(name = "created", nullable = false, updatable = false)
  private Instant created = Instant.now();

  @NonNull
  @Column(name = "oauth_key", nullable = false, updatable = false, unique = true, length = 30)
  private String oauthKey;

  @Column(name = "display_name", length = 100)
  private String displayName;

  /**
   * Returns the unique identifier for this user profile.
   *
   * @return User profile ID.
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the timestamp when this user profile was created.
   *
   * @return Creation timestamp.
   */
  @NonNull
  public Instant getCreated() {
    return created;
  }

  /**
   * Returns the OAuth2 key associated with this user profile.
   *
   * @return OAuth2 key.
   */
  @NonNull
  public String getOauthKey() {
    return oauthKey;
  }

  /**
   * Sets the OAuth2 key for this user profile.
   *
   * @param oauthKey OAuth2 key.
   */
  public void setOauthKey(@NonNull String oauthKey) {
    this.oauthKey = oauthKey;
  }

  /**
   * Returns the display name for this user profile.
   *
   * @return Display name, or {@code null} if not set.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Sets the display name for this user profile.
   *
   * @param displayName Display name.
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

}
