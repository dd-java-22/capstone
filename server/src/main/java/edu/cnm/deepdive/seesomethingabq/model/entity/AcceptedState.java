package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(
  name = "accepted_state",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_accepted_state_status_tag", columnNames = "status_tag")
  }
)
public class AcceptedState {

  @Id
  @Column(name = "accepted_state_id", updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String statusTag;

  @Column(nullable = false)
  private String statusTagDescription;

  // used AI to help with OneToMany annotation
  @OneToMany(mappedBy = "acceptedState", fetch = FetchType.LAZY)
  @OrderBy("timeLastModified DESC")
  private final List<IssueReport> issueReports = new LinkedList<>();

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

  public List<IssueReport> getIssueReports() {
    return issueReports;
  }
}
