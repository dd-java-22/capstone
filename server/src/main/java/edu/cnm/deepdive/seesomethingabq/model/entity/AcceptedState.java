package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
  name = "accepted_state",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_accepted_state_status_tag", columnNames = "status_tag")
  }
)
public class AcceptedState {

  @Id
  @Column(name = "accepted_state_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "status_tag", nullable = false)
  private String statusTag;

  @Column(name = "status_tag_description", nullable = false)
  private String statusTagDescription;

  // used AI to help with OneToMany annotation
  @OneToMany(mappedBy = "acceptedState")
  private final Set<IssueReport> issueReports = new HashSet<>();

  public Long getId() {
    return id;
  }

  public String getStatusTag() {
    return statusTag;
  }

  public void setStatusTag(String statusTag) {
    this.statusTag = statusTag;
  }

  public String getStatusTagDescription() {
    return statusTagDescription;
  }

  public void setStatusTagDescription(String statusTagDescription) {
    this.statusTagDescription = statusTagDescription;
  }

  public Set<IssueReport> getIssueReports() {
    return issueReports;
  }
}
