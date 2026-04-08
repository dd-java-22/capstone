package edu.cnm.deepdive.seesomethingabq.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.LinkedList;
import java.util.List;

/**
 * JPA entity representing a category/tag that can be associated with an {@link IssueReport}.
 */
@Entity
@Table(
  name = "issue_type",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_issue_type_issue_type_tag", columnNames = "issue_type_tag")
  }
)
@JsonPropertyOrder({"issueTypeTag", "issueTypeDescription"})
public class IssueType {

  @Id
  @Column(name = "issue_type_id", nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @Column(nullable = false)
  private String issueTypeTag;

  @Column(nullable = false)
  private String issueTypeDescription;

  // used AI to help with ManyToMany annotation
  @ManyToMany(mappedBy = "issueTypes", fetch = FetchType.LAZY)
  @JsonIgnore
  private final List<IssueReport> issueReports = new LinkedList<>();

  /**
   * Returns the database identifier for this issue type.
   *
   * @return primary key value.
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the issue type tag.
   *
   * @return issue type tag.
   */
  public String getIssueTypeTag() {
    return issueTypeTag;
  }

  /**
   * Sets the issue type tag.
   *
   * @param issueTypeTag issue type tag.
   */
  public void setIssueTypeTag(String issueTypeTag) {
    this.issueTypeTag = issueTypeTag;
  }

  /**
   * Returns the issue type description.
   *
   * @return issue type description.
   */
  public String getIssueTypeDescription() {
    return issueTypeDescription;
  }

  /**
   * Sets the issue type description.
   *
   * @param issueTypeDescription issue type description.
   */
  public void setIssueTypeDescription(String issueTypeDescription) {
    this.issueTypeDescription = issueTypeDescription;
  }

  /**
   * Returns the issue reports currently associated with this issue type.
   *
   * @return associated issue reports.
   */
  @JsonIgnore
  public List<IssueReport> getIssueReports() {
    return issueReports;
  }
}
