package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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

  // TODO: 3/16/2026 consider adding UUIDs for UserProfile and IssueReport - will need prepersist

  @Id
  @Column(name = "issue_report_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // TODO: 3/16/2026 add optional to other fields that need it
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "user_profile_id", nullable = false, updatable = false)
  private UserProfile userProfile;

  @OneToOne
  @JoinColumn(name = "report_location_id", nullable = false)
  private ReportLocation reportLocation;

  @ManyToOne
  @JoinColumn(name = "accepted_state_id", nullable = false)
  private AcceptedState acceptedState;

  // used AI to help with JoinTable annotation
  @ManyToMany
  @JoinTable(
    name = "issue_report_issue_type",
    joinColumns = @JoinColumn(name = "issue_report_id"),
    inverseJoinColumns = @JoinColumn(name = "issue_type_id")
  )
  private final Set<IssueType> issueTypes = new HashSet<>();

  @CreationTimestamp
  @Column(name = "time_first_reported", nullable = false)
  private Instant timeFirstReported;

  @UpdateTimestamp
  @Column(name = "time_last_modified", nullable = false)
  private Instant timeLastModified;

  @Column(name = "text_description", nullable = false)
  private String textDescription; // User explanation of issue

  // used AI to help with OneToMany annotation
  @OneToMany(mappedBy = "issueReport") // this is confusing - issueReport here actually refers to the ReportImage.issueReport field
  private final Set<ReportImage> reportImages = new HashSet<>();

  public Long getId() {
    return id;
  }

  public UserProfile getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

  public ReportLocation getReportLocation() {
    return reportLocation;
  }

  public void setReportLocation(ReportLocation reportLocation) {
    this.reportLocation = reportLocation;
  }

  public AcceptedState getAcceptedState() {
    return acceptedState;
  }

  public void setAcceptedState(AcceptedState acceptedState) {
    this.acceptedState = acceptedState;
  }

  public Set<IssueType> getIssueTypes() {
    return issueTypes;
  }

  public Instant getTimeFirstReported() {
    return timeFirstReported;
  }

  public Instant getTimeLastModified() {
    return timeLastModified;
  }

  public String getTextDescription() {
    return textDescription;
  }

  public void setTextDescription(String textDescription) {
    this.textDescription = textDescription;
  }

  public Set<ReportImage> getReportImages() {
    return reportImages;
  }
}

