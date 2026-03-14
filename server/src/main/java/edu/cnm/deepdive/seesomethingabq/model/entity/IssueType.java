package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
  name = "issue_type",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_issue_type_issue_type_tag", columnNames = "issue_type_tag")
  }
)
public class IssueType {

  @Id
  @Column(name = "issue_type_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long issueTypeId;

  @Column(
    name = "issue_type_tag",
    nullable = false,
    unique = true
  )
  private String issueTypeTag;

  @Column(name = "issue_type_description", nullable = false)
  private String issueTypeDescription;

  // used AI to help with ManyToMany annotation
  @ManyToMany(mappedBy = "issueTypes")
  private final Set<IssueReport> issueReports = new HashSet<>();

  public Long getIssueTypeId() {
    return issueTypeId;
  }

  public String getIssueTypeTag() {
    return issueTypeTag;
  }

  public void setIssueTypeTag(String issueTypeTag) {
    this.issueTypeTag = issueTypeTag;
  }

  public String getIssueTypeDescription() {
    return issueTypeDescription;
  }

  public void setIssueTypeDescription(String issueTypeDescription) {
    this.issueTypeDescription = issueTypeDescription;
  }

  public Set<IssueReport> getIssueReports() {
    return issueReports;
  }
}
