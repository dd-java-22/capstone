package edu.cnm.deepdive.seesomethingabq.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * JPA entity representing a user-submitted issue report, including location, status, and images.
 */
@Entity
@Table(
  name = "issue_report",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_issue_report_report_location_id", columnNames = "report_location_id")
  },
  indexes = {
    @Index(name = "ix_issue_report_user_profile_id", columnList = "user_profile_id"),
    @Index(name = "ix_issue_report_accepted_state_id", columnList = "accepted_state_id"),
    @Index(name = "ix_issue_report_time_first_reported", columnList = "time_first_reported"),
    @Index(name = "ix_issue_report_time_last_modified", columnList = "time_last_modified")
  }
)
public class IssueReport {

  @Id
  @Column(name = "issue_report_id", updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @Column(name = "issue_report_external_id", updatable = false)
  private UUID externalId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_profile_id", nullable = false, updatable = false)
  private UserProfile userProfile;

  // TODO: 3/26/2026 Revisit cascade strategy for reportLocation (ALL vs PERSIST/MERGE/REMOVE). 
  @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "report_location_id", nullable = false)
  private ReportLocation reportLocation;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "accepted_state_id", nullable = false)
  private AcceptedState acceptedState;

  // used AI to help with JoinTable annotation
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "issue_report_issue_type",
    joinColumns = @JoinColumn(
      name = "issue_report_id",
      nullable = false
    ),
    inverseJoinColumns = @JoinColumn(
      name = "issue_type_id",
      nullable = false
    )
  )
  private final List<IssueType> issueTypes = new LinkedList<>();

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant timeFirstReported;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant timeLastModified;

  @Column(nullable = false)
  private String textDescription; // User explanation of issue

  // used AI to help with OneToMany annotation
  @OneToMany(mappedBy = "issueReport", fetch = FetchType.EAGER)
  @OrderBy("albumOrder DESC")
  private final List<ReportImage> reportImages = new LinkedList<>();

  /**
   * Returns the database identifier for this report.
   *
   * @return primary key value.
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the external identifier for this report.
   *
   * @return external ID.
   */
  public UUID getExternalId() {
    return externalId;
  }

  /**
   * Returns the user profile that created this report.
   *
   * @return user profile.
   */
  public UserProfile getUserProfile() {
    return userProfile;
  }

  /**
   * Sets the user profile that created this report.
   *
   * @param userProfile user profile.
   */
  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

  /**
   * Returns the report location.
   *
   * @return report location.
   */
  public ReportLocation getReportLocation() {
    return reportLocation;
  }

  /**
   * Sets the report location.
   *
   * @param reportLocation report location.
   */
  public void setReportLocation(ReportLocation reportLocation) {
    this.reportLocation = reportLocation;
  }

  /**
   * Returns the accepted-state/status of this report.
   *
   * @return accepted state.
   */
  public AcceptedState getAcceptedState() {
    return acceptedState;
  }

  /**
   * Sets the accepted-state/status of this report.
   *
   * @param acceptedState accepted state.
   */
  public void setAcceptedState(AcceptedState acceptedState) {
    this.acceptedState = acceptedState;
  }

  /**
   * Returns the issue types associated with this report.
   *
   * @return associated issue types.
   */
  public List<IssueType> getIssueTypes() {
    return issueTypes;
  }

  /**
   * Returns the time the report was first persisted.
   *
   * @return first-reported timestamp.
   */
  public Instant getTimeFirstReported() {
    return timeFirstReported;
  }

  /**
   * Returns the time the report was last modified.
   *
   * @return last-modified timestamp.
   */
  public Instant getTimeLastModified() {
    return timeLastModified;
  }

  /**
   * Returns the user-supplied issue description.
   *
   * @return description text.
   */
  public String getTextDescription() {
    return textDescription;
  }

  /**
   * Sets the user-supplied issue description.
   *
   * @param textDescription description text.
   */
  public void setTextDescription(String textDescription) {
    this.textDescription = textDescription;
  }

  /**
   * Returns the images associated with this report.
   *
   * @return associated images.
   */
  public List<ReportImage> getReportImages() {
    return reportImages;
  }

  @PrePersist
  void onCreate() {
    this.externalId = UUID.randomUUID();
  }
}

