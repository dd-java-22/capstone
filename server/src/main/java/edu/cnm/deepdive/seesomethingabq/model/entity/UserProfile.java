package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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
  @Column(name = "user_profile_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userProfileId;

  @Column(
    name = "oauth_key",
    nullable = false,
    unique = true
  )
  private String oauthKey;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "is_manager", nullable = false)
  private Boolean isManager;

  @CreationTimestamp
  @Column(
    name = "time_created",
    nullable = false,
    updatable = false
  )
  private Instant timeCreated;

  @Column(name = "user_enabled", nullable = false)
  private Boolean userEnabled;

  // used AI to help with OneToMany annotation
  @OneToMany(mappedBy = "userProfile")
  private final Set<IssueReport> issueReports = new HashSet<>();

  public Long getUserProfileId() {
    return userProfileId;
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

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getIsManager() {
    return isManager;
  }

  public void setIsManager(Boolean isManager) {
    this.isManager = isManager;
  }

  public Instant getTimeCreated() {
    return timeCreated;
  }

  public Boolean getUserEnabled() {
    return userEnabled;
  }

  public void setUserEnabled(Boolean userEnabled) {
    this.userEnabled = userEnabled;
  }

  public Set<IssueReport> getIssueReports() {
    return issueReports;
  }
}
