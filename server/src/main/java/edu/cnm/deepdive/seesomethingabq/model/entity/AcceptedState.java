package edu.cnm.deepdive.seesomethingabq.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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

/**
 * JPA entity representing the accepted-state/status associated with an {@link IssueReport}.
 */
@Entity
@Table(
  name = "accepted_state",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_accepted_state_status_tag", columnNames = "status_tag")
  }
)
@JsonPropertyOrder({"statusTag", "statusTagDescription"})
public class AcceptedState {

  @Id
  @Column(name = "accepted_state_id", updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @Column(nullable = false)
  private String statusTag;

  @Column(nullable = false)
  private String statusTagDescription;

  // used AI to help with OneToMany annotation
  @OneToMany(mappedBy = "acceptedState", fetch = FetchType.LAZY)
  @OrderBy("timeLastModified DESC")
  @JsonIgnore
  private final List<IssueReport> issueReports = new LinkedList<>();

  /**
   * Returns the database identifier for this accepted state.
   *
   * @return primary key value.
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the status tag value.
   *
   * @return status tag.
   */
  public String getStatusTag() {
    return statusTag;
  }

  /**
   * Sets the status tag value.
   *
   * @param statusTag status tag.
   */
  public void setStatusTag(String statusTag) {
    this.statusTag = statusTag;
  }

  /**
   * Returns the human-readable status description.
   *
   * @return status description.
   */
  public String getStatusTagDescription() {
    return statusTagDescription;
  }

  /**
   * Sets the human-readable status description.
   *
   * @param statusTagDescription status description.
   */
  public void setStatusTagDescription(String statusTagDescription) {
    this.statusTagDescription = statusTagDescription;
  }

  /**
   * Returns the issue reports currently associated with this accepted state.
   *
   * @return associated issue reports.
   */
  @JsonIgnore
  public List<IssueReport> getIssueReports() {
    return issueReports;
  }
}
