package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "issue_report")
public class IssueReport {

  @Id
  @Column(name = "issue_report_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long issueReportId;

  @ManyToOne
  @JoinColumn(name = "user_profile_id", nullable = false)
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
}

