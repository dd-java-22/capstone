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

  @PrePersist
  void onCreate() {
    this.externalId = UUID.randomUUID();
  }
}
