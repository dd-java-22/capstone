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
  private Long id;

  @Column(name = "user_profile_external_id", updatable = false)
  private UUID externalId;

  @Column(nullable = false, updatable = false)
  private String oauthKey;

  @Column(nullable = false)
  private String displayName;

  @Column(nullable = false)
  private String email;

  @Column(nullable = true, updatable = true)
  private URL avatar;

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
  private final List<IssueReport> issueReports = new LinkedList<>();

  public Long getId() {
    return id;
  }

  public String getOauthKey() {
    return oauthKey;
  }

  public void setOauthKey(String oauthKey) {
    this.oauthKey = oauthKey;
  }

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

  public URL getAvatar() {
    return avatar;
  }

  public void setAvatar(URL avatar) {
    this.avatar = avatar;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isManager() {
    return isManager;
  }

  public void setIsManager(boolean isManager) {
    this.isManager = isManager;
  }

  public Instant getTimeCreated() {
    return timeCreated;
  }

  public boolean getUserEnabled() {
    return userEnabled;
  }

  public void setUserEnabled(boolean userEnabled) {
    this.userEnabled = userEnabled;
  }

  public List<IssueReport> getIssueReports() {
    return issueReports;
  }

  @PrePersist
  void onCreate() {
    this.externalId = UUID.randomUUID();
  }
}
