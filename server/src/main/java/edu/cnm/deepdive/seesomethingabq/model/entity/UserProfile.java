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

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.net.URL;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

/**
 * JPA entity representing a user profile backed by an OAuth subject key.
 */
@Entity
@Table(
  name = "user_profile",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_profile_oauth_key", columnNames = "oauth_key")
  },
  indexes = {
    @Index(name = "ix_user_profile_email", columnList = "email"),
    @Index(name = "ix_user_profile_user_enabled", columnList = "user_enabled")
  }
)
public class UserProfile {

  @Id
  @Column(name = "user_profile_id", updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @Column(name = "user_profile_external_id", updatable = false)
  private UUID externalId;

  @Column(nullable = false, updatable = false)
  @JsonIgnore
  private String oauthKey;

  @Column(nullable = false)
  private String displayName;

  @Column(nullable = false)
  private String email;

  @Column(nullable = true, updatable = true)
  private URL avatar;

  @Column(name = "avatar_key")
  @JsonIgnore
  private String avatarKey;

  @Column(name = "avatar_mime_type")
  @JsonIgnore
  private String avatarMimeType;

  @Column(nullable = false)
  private boolean isManager;

  @CreationTimestamp
  @Column(
    nullable = false,
    updatable = false
  )
  private Instant timeCreated;

  @Column(nullable = false)
  private boolean userEnabled;

  // used AI to help with OneToMany annotation
  @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY)
  @OrderBy("timeLastModified DESC")
  @JsonIgnore
  private final List<IssueReport> issueReports = new LinkedList<>();

  /**
   * Returns the database identifier for this user profile.
   *
   * @return primary key value.
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the external identifier for this user profile.
   *
   * @return external ID.
   */
  public UUID getExternalId() {
    return externalId;
  }

  /**
   * Returns the OAuth subject key for this user.
   *
   * @return OAuth key.
   */
  public String getOauthKey() {
    return oauthKey;
  }

  /**
   * Sets the OAuth subject key for this user.
   *
   * @param oauthKey OAuth key.
   */
  public void setOauthKey(String oauthKey) {
    this.oauthKey = oauthKey;
  }

  /**
   * Returns the display name for this user.
   *
   * @return display name.
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

  /**
   * Returns the avatar URL, if configured.
   *
   * @return avatar URL.
   */
  public URL getAvatar() {
    return avatar;
  }

  /**
   * Sets the avatar URL.
   *
   * @param avatar avatar URL.
   */
  public void setAvatar(URL avatar) {
    this.avatar = avatar;
  }

  /**
   * Returns the storage key for a locally stored custom avatar, if set.
   *
   * @return avatar storage key (or {@code null}).
   */
  public String getAvatarKey() {
    return avatarKey;
  }

  /**
   * Sets the storage key for a locally stored custom avatar.
   *
   * @param avatarKey storage key.
   */
  public void setAvatarKey(String avatarKey) {
    this.avatarKey = avatarKey;
  }

  /**
   * Returns the MIME type for a locally stored custom avatar, if set.
   *
   * @return avatar MIME type (or {@code null}).
   */
  public String getAvatarMimeType() {
    return avatarMimeType;
  }

  /**
   * Sets the MIME type for a locally stored custom avatar.
   *
   * @param avatarMimeType avatar MIME type.
   */
  public void setAvatarMimeType(String avatarMimeType) {
    this.avatarMimeType = avatarMimeType;
  }

  /**
   * Returns the email address.
   *
   * @return email address.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email address.
   *
   * @param email email address.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Returns whether this user has manager privileges.
   *
   * @return {@code true} if manager; {@code false} otherwise.
   */
  public boolean isManager() {
    return isManager;
  }

  /**
   * Sets whether this user has manager privileges.
   *
   * @param isManager {@code true} if manager; {@code false} otherwise.
   */
  public void setIsManager(boolean isManager) {
    this.isManager = isManager;
  }

  /**
   * Returns the time this user profile was created.
   *
   * @return creation timestamp.
   */
  public Instant getTimeCreated() {
    return timeCreated;
  }

  /**
   * Returns whether the user account is enabled.
   *
   * @return {@code true} if enabled; {@code false} otherwise.
   */
  public boolean getUserEnabled() {
    return userEnabled;
  }

  /**
   * Sets whether the user account is enabled.
   *
   * @param userEnabled {@code true} to enable; {@code false} to disable.
   */
  public void setUserEnabled(boolean userEnabled) {
    this.userEnabled = userEnabled;
  }

  /**
   * Returns the issue reports created by this user.
   *
   * @return issue reports.
   */
  public List<IssueReport> getIssueReports() {
    return issueReports;
  }

  @PrePersist
  void onCreate() {
    this.externalId = UUID.randomUUID();
  }
}
