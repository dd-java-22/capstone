package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
  @Column(name = "user_profile_id", updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // TODO: 3/16/2026 rename all id fields to just 'id'
  // TODO: 3/16/2026 remove name on fields where name is the same
  // TODO: 3/16/2026 check that updateable = false is applied everywhere that needs it
  @Column(name = "oauth_key", nullable = false, updatable = false)
  private String oauthKey;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "is_manager", nullable = false)
  private boolean isManager;

  @CreationTimestamp
  @Column(
    name = "time_created",
    nullable = false,
    updatable = false
  )
  private Instant timeCreated;

  @Column(name = "user_enabled", nullable = false)
  private boolean userEnabled;

  // used AI to help with OneToMany annotation
  // TODO: 3/16/2026 add fetchType and orderBy to fields that need it
  // TODO: 3/16/2026 double check collection types
  @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY)
  @OrderBy("timeFirstReported DESC")
  private final List<IssueReport> issueReports = new LinkedList<>();

  public Long getUserProfileId() {
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

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
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
}
